package org.eclipse.babel.core.message.manager;

import org.eclipse.babel.tapiji.translator.rbe.babel.bundle.IMessagesBundle;

public interface IMessagesEditorListener {

	void onSave();
	
	void onModify();
	
	void onResourceChanged(IMessagesBundle bundle);
	
}
