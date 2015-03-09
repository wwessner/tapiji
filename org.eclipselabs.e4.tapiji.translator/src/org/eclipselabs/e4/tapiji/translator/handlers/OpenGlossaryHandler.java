package org.eclipselabs.e4.tapiji.translator.handlers;


import java.io.File;
import javax.inject.Named;
import org.eclipse.babel.editor.widgets.suggestion.exception.InvalidConfigurationSetting;
import org.eclipse.babel.editor.widgets.suggestion.provider.StringConfigurationSetting;
import org.eclipse.babel.editor.widgets.suggestion.provider.SuggestionProviderUtils;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.core.GlossaryManager;
import org.eclipselabs.e4.tapiji.utils.FileUtils;


public class OpenGlossaryHandler {

  private static final String TAG = OpenGlossaryHandler.class.getSimpleName();

  @Execute
  public void execute(@Named(IServiceConstants.ACTIVE_SHELL) final Shell shell) {
    final String[] fileNames = FileUtils.queryFileName(shell, "Open Glossary", SWT.OPEN, FileUtils.XML_FILE_ENDING);
    if (fileNames != null) {
      final String fileName = fileNames[0];
      if (!FileUtils.isGlossary(fileName)) {
        MessageDialog.openError(shell, "Cannot open Glossary", "The choosen file does not represent a Glossary!");
        Log.i(TAG, String.format("Cannot open Glossary %s", fileName));
        return;
      }

      GlossaryManager.loadGlossary(new File(fileName));
      try {
        SuggestionProviderUtils.updateConfigurationSetting("glossaryFile", new StringConfigurationSetting(fileName));
      } catch (final InvalidConfigurationSetting exception) {
        Log.e(TAG, exception);
      }
    }
  }
}
