package io.jayms.dbsc.qb;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import io.jayms.dbsc.model.Column;
import io.jayms.dbsc.model.Table;
import lombok.Getter;

public class QueryBuilderContext {

	@Getter private Multimap<Table, Column> fieldsToSelect;
	@Getter private List<Join> joins;
	
	public QueryBuilderContext() {
		this.fieldsToSelect = HashMultimap.create();
		this.joins = new ArrayList<>();
	}
	
}
