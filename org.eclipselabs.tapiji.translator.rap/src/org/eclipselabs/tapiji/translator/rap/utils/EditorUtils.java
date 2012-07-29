package org.eclipselabs.tapiji.translator.rap.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipselabs.tapiji.translator.actions.FileOpenAction;
import org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;

public class EditorUtils {
	public static final String MSG_EDITOR_ID = FileOpenAction.RESOURCE_BUNDLE_EDITOR;
	
	public static void openRB(ResourceBundle rb) {
		if (isRBOpened(rb) || rb.getLocalFiles().isEmpty())
			return;
		
		try {
			getActivePage().openEditor( new FileEditorInput(FileRAPUtils.getIFile(rb.getLocalFiles().get(0))), 
					MSG_EDITOR_ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}	
	}
	
	public static boolean closeRB(ResourceBundle rb, boolean save) {
		List<IEditorReference> openedEditors = getOpenedEditors(rb);
		
		if (openedEditors.isEmpty())
			return false;
		
		return getActivePage().closeEditor(openedEditors.get(0).getEditor(false), save);		
	}
	
	public static IWorkbenchPage getActivePage() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}
		
	public static boolean isRBOpened(ResourceBundle rb) {
		if (getOpenedEditors(rb).isEmpty())
			return false;
		return true;
	}
	
	public static List<IEditorReference> getOpenedEditors(ResourceBundle rb) {
		List<IEditorReference> openedRB = new ArrayList<IEditorReference>();
		try {
			for (IEditorReference editor : getActivePage().getEditorReferences()) {
				if (editor.getEditorInput() instanceof IFileEditorInput) {
					IFileEditorInput editorInput = (IFileEditorInput) editor.getEditorInput();
					String ifilePath = editorInput.getFile().getLocation().toOSString();
					
					for (PropertiesFile file : rb.getLocalFiles())
						if (ifilePath.equals(file.getPath()))
								openedRB.add(editor);
				}
			}
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return openedRB;
	}
}
