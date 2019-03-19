package io.jayms.dbsc.qb;

import io.jayms.dbsc.model.Column;
import io.jayms.dbsc.model.Table;
import lombok.Getter;

public class Join {

	@Getter private Table table1;
	@Getter private Table table2;
	@Getter private Column joinedBy;
	
	public Join(Table table1, Table table2, Column joinedBy) {
		this.table1 = table1;
		this.table2 = table2;
		this.joinedBy = joinedBy;
	}
}
