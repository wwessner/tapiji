package at.ac.tuwien.inso.eclipse.i18n.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import at.ac.tuwien.inso.eclipse.i18n.Activator;

public class InternationalizationNature implements IProjectNature {

	private static final String NATURE_ID = Activator.PLUGIN_ID + ".stringLiteralAuditor";
	private IProject project;
	
	@Override
	public void configure() throws CoreException {
		StringLiteralAuditor.addBuilderToProject(project);
		new Job ("Audit source files for constant string literals") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					project.build ( StringLiteralAuditor.FULL_BUILD,
							StringLiteralAuditor.BUILDER_ID,
							null,
							monitor);
				} catch (CoreException e) {
					e.printStackTrace();
				}
				return Status.OK_STATUS;
			}
			
		}.schedule();
	}

	@Override
	public void deconfigure() throws CoreException {
		StringLiteralAuditor.removeBuilderFromProject(project);
	}

	@Override
	public IProject getProject() {
		return project;
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;
	}

	public static void addNature(IProject project) {
		if (!project.isOpen())
			return;
		
		IProjectDescription description = null;
		
		try {
			description = project.getDescription();
		} catch (CoreException e) {
			e.printStackTrace();
			return;
		}
		
		// Check if the project has already this nature
		List<String> newIds = new ArrayList<String> ();
		newIds.addAll (Arrays.asList(description.getNatureIds()));
		int index = newIds.indexOf (NATURE_ID);
		if (index != -1)
			return;
		
		// Add the nature
		newIds.add (NATURE_ID);
		description.setNatureIds (newIds.toArray (new String[newIds.size()]));
		
		try {
			project.setDescription(description, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public static boolean hasNature (IProject project) {
		try {
			return project.isOpen () && project.hasNature(NATURE_ID);
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void removeNature (IProject project) {
		if (!project.isOpen())
			return;
		
		IProjectDescription description = null;
		
		try {
			description = project.getDescription();
		} catch (CoreException e) {
			e.printStackTrace();
			return;
		}
		
		// Check if the project has already this nature
		List<String> newIds = new ArrayList<String> ();
		newIds.addAll (Arrays.asList(description.getNatureIds()));
		int index = newIds.indexOf (NATURE_ID);
		if (index == -1)
			return;
		
		// remove the nature
		newIds.remove (NATURE_ID);
		description.setNatureIds (newIds.toArray (new String[newIds.size()]));
		
		try {
			project.setDescription(description, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
