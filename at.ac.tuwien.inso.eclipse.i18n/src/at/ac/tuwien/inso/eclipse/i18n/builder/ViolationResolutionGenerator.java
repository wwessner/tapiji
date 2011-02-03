package at.ac.tuwien.inso.eclipse.i18n.builder;

import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;

import at.ac.tuwien.inso.eclipse.i18n.extensions.I18nResourceAuditor;
import at.ac.tuwien.inso.eclipse.i18n.model.exception.NoSuchResourceAuditorException;

public class ViolationResolutionGenerator implements IMarkerResolutionGenerator2 {

	@Override
	public boolean hasResolutions(IMarker marker) {
		return true;
	}

	@Override
	public IMarkerResolution[] getResolutions(IMarker marker) {
		String contextId = marker.getAttribute("context", "");
		
		// find resolution generator for the given context
		try {
			I18nResourceAuditor auditor = StringLiteralAuditor.getI18nAuditorByContext(contextId);
			List<IMarkerResolution> resolutions = auditor.getMarkerResolutions(marker, marker.getAttribute("cause", -1));
			return resolutions.toArray(new IMarkerResolution[resolutions.size()]);
		} catch (NoSuchResourceAuditorException e) {}
		
		return new IMarkerResolution[0]; 
	}

}
