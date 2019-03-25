package io.jayms.dbsc.ui.comp.treeitem;

import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.scene.input.MouseEvent;

public class DBSCTreeEventDispatcher implements EventDispatcher {

	private final EventDispatcher originalDispatcher;
	
	public DBSCTreeEventDispatcher(EventDispatcher originalDispatcher) {
		this.originalDispatcher = originalDispatcher;
	}
	
	@Override
	public Event dispatchEvent(Event event, EventDispatchChain tail) {
		if (event instanceof MouseEvent) {
			if (event.getEventType().equals(MouseEvent.MOUSE_CLICKED)) {
				event.consume();
			}
		}
		return originalDispatcher.dispatchEvent(event, tail);
	}
	
}
