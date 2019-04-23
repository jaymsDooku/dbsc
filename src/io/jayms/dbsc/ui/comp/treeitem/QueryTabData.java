package io.jayms.dbsc.ui.comp.treeitem;

import io.jayms.dbsc.model.Query;
import lombok.Getter;
import lombok.Setter;

public class QueryTabData {

	@Getter private Query query;
	@Getter @Setter private int runningTaskId;
	
	public QueryTabData(Query query) {
		this.query = query;
		this.runningTaskId = -1;
	}
	
}
