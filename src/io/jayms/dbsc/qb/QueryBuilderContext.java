package io.jayms.dbsc.qb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import io.jayms.dbsc.model.Column;
import io.jayms.dbsc.model.Table;
import lombok.Getter;

public class QueryBuilderContext {

	@Getter private Multimap<Table, Column> fieldsToSelect;
	@Getter private Map<Table, Join> joins;
	
	public QueryBuilderContext() {
		this.fieldsToSelect = HashMultimap.create();
		this.joins = new HashMap<>();
	} 
	
	public String generateQuery(boolean applyFormatting) {
		StringBuilder result = new StringBuilder();
		result.append("SELECT ");
		
		StringBuilder fieldsPart = new StringBuilder();
		for (Table table : fieldsToSelect.keySet()) {
			List<Column> columns = Lists.newArrayList(fieldsToSelect.get(table));
			
			for (int i = 0; i < columns.size(); i++) {
				Column col = columns.get(i);
				fieldsPart.append(table.getName() + "." + col.getName());
				if (i < columns.size() - 1) {
					fieldsPart.append(", ");
				}
			}
		}
		
		result.append(fieldsPart);
		result.append(" FROM ");
		
		
		StringBuilder tablesPart = new StringBuilder();
		
		
		result.append(tablesPart);
		
		return result.toString();
	}
	
}
