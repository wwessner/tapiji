package at.ac.tuwien.inso.eclipse.i18n.builder.quickfix;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.IWizardDescriptor;

import at.ac.tuwien.inso.eclipse.i18n.builder.StringLiteralAuditor;
import at.ac.tuwien.inso.eclipse.i18n.model.manager.ResourceBundleManager;
import at.ac.tuwien.inso.eclipse.i18n.util.ResourceUtils;
import at.ac.tuwien.inso.eclipse.rbe.ui.wizards.IResourceBundleWizard;

public class CreateResourceBundle implements ICompletionProposal,
		IMarkerResolution2 {

	private IResource resource;
	private int start;
	private int end;
	private String key;
	private boolean jsfContext;
	private final String newBunldeWizard = "com.essiembre.eclipse.rbe.ui.wizards.ResourceBundleWizard";

	public CreateResourceBundle(String key, IResource resource, int start, int end) {
		this.key = ResourceUtils.deriveNonExistingRBName(key, ResourceBundleManager.getManager( resource.getProject() ));
		this.resource = resource;
		this.start = start;
		this.end = end;
		this.jsfContext = jsfContext;
	}

	@Override
	public String getDescription() {
		return "Creates a new Resource-Bundle with the id '" + key + "'";
	}

	@Override
	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLabel() {
		return "Create Resource-Bundle '" + key + "'";
	}

	@Override
	public void run(IMarker marker) {
		runAction();
	}

	@Override
	public void apply(IDocument document) {
		runAction();
	}
	
	private void runAction () {
		// First see if this is a "new wizard".
		IWizardDescriptor descriptor = PlatformUI.getWorkbench()
				.getNewWizardRegistry().findWizard(newBunldeWizard);
		// If not check if it is an "import wizard".
		if (descriptor == null) {
			descriptor = PlatformUI.getWorkbench().getImportWizardRegistry()
					.findWizard(newBunldeWizard);
		}
		// Or maybe an export wizard
		if (descriptor == null) {
			descriptor = PlatformUI.getWorkbench().getExportWizardRegistry()
					.findWizard(newBunldeWizard);
		}
		try {
			// Then if we have a wizard, open it.
			if (descriptor != null) {
				IWizard wizard = descriptor.createWizard();
				if (!(wizard instanceof IResourceBundleWizard))
					return;
				
				IResourceBundleWizard rbw = (IResourceBundleWizard) wizard;
				String[] keySilbings = key.split("\\.");
				String rbName = keySilbings[keySilbings.length-1];
				String packageName = "";
				
				rbw.setBundleId(rbName);
				
				// Set the default path according to the specified package name
				String pathName = "";
				if (keySilbings.length > 1) {
					try { 
						IJavaProject jp = JavaCore.create(resource.getProject());
						packageName = key.substring(0, key.lastIndexOf("."));
						
						for (IPackageFragmentRoot fr : jp.getAllPackageFragmentRoots()) {
							IPackageFragment pf = fr.getPackageFragment(packageName);
							if (pf.exists()) {
								pathName = pf.getResource().getFullPath().removeFirstSegments(0).toOSString();
								break;
							}
						}
					} catch (Exception e) {
						pathName = "";
					}
				}
				
				try { 
					IJavaProject jp = JavaCore.create(resource.getProject());
					if (pathName.trim().equals("")) {
						for (IPackageFragmentRoot fr : jp.getAllPackageFragmentRoots()) {
							if (!fr.isReadOnly()) {
								pathName = fr.getResource().getFullPath().removeFirstSegments(0).toOSString();
								break;
							}
						}
					}
				} catch (Exception e) {
					pathName = "";
				}
				
				rbw.setDefaultPath (pathName);
					
				WizardDialog wd = new WizardDialog(Display.getDefault()
						.getActiveShell(), wizard);
				wd.setTitle(wizard.getWindowTitle());
				if (wd.open() == WizardDialog.OK) {
					(new StringLiteralAuditor()).buildProject(null, resource.getProject());
					(new StringLiteralAuditor()).buildResource(resource, null);
					
					ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager(); 
					IPath path = resource.getRawLocation(); 
					try {
						bufferManager.connect(path, null); 
						ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(path);
						IDocument document = textFileBuffer.getDocument(); 
					
						if (document.get().charAt(start-1) == '"' && document.get().charAt(start) != '"') {
							start --;
							end ++;
						}
						if (document.get().charAt(end+1) == '"' && document.get().charAt(end) != '"')
							end ++;
												
						document.replace(start, end-start, 
								"\"" + 
								(packageName.equals("") ? "" : packageName + ".") + rbName + 
								"\"");
						
						textFileBuffer.commit(null, false);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							bufferManager.disconnect(path, null);
						} catch (CoreException e) {
							e.printStackTrace();
						} 
					}
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getAdditionalProposalInfo() {
		return getDescription();
	}

	@Override
	public IContextInformation getContextInformation() {
		return null;
	}

	@Override
	public String getDisplayString() {
		return getLabel();
	}

	@Override
	public Point getSelection(IDocument document) {
		return null;
	}

}
