package io.jayms.dbsc.qb;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
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
	
	private LinkedList<Join> getOrderedJoins(LinkedList<Join> ordered, Join firstJoin) {
		if (firstJoin == null) return ordered;
		
		ordered.add(firstJoin);
		System.out.println("firstJoin: " + firstJoin);
		Table nextTable = firstJoin.getJoinCircle2().getTable();
		System.out.println("nextTable: " + nextTable);
		System.out.println("joins: " + joins);
		Join nextJoin = joins.get(nextTable);
		System.out.println("nextJoin: " + nextJoin);
		return getOrderedJoins(ordered, nextJoin);
	}
	
	private LinkedList<Join> getOrderedJoins() {
		LinkedList<Join> result = new LinkedList<>();
		
		if (joins.isEmpty()) return result;
		
		List<Join> allJoins = Lists.newArrayList(joins.values());
		result = getOrderedJoins(result, allJoins.get(0));
		
		return result;
	}
	
	public String generateQuery(boolean applyFormatting) {
		StringBuilder result = new StringBuilder();
		result.append("SELECT ");
		
		StringBuilder fieldsPart = new StringBuilder();
		List<Table> tables = Lists.newArrayList(fieldsToSelect.keySet());
		for (int t = 0; t < tables.size(); t++) {
			Table table = tables.get(t);
			List<Column> columns = Lists.newArrayList(fieldsToSelect.get(table));
			
			for (int i = 0; i < columns.size(); i++) {
				Column col = columns.get(i);
				fieldsPart.append(table.getName() + "." + col.getName());
				if (i < columns.size() && t < tables.size() - 1) {
					fieldsPart.append(", ");
				} else if (i < columns.size() - 1 && t == tables.size() - 1) {
					fieldsPart.append(", ");
				}
			}
		}
		
		result.append(fieldsPart);
		result.append(" \nFROM ");
		
		StringBuilder tablesPart = new StringBuilder();
		
		if (joins.isEmpty()) {
			for (int i = 0; i < tables.size(); i++) {
				Table table = tables.get(i);
				tablesPart.append(table.getName());
				if (i < tables.size() - 1) {
					tablesPart.append(", ");
				}
			}
		} else {
			LinkedList<Join> orderedJoins = getOrderedJoins();
			System.out.println("orderedJoins: " + orderedJoins);
			for (int i = 0; i < orderedJoins.size(); i++) {
				Join join = orderedJoins.get(i);
				JoinCircle joinCircle1 = join.getJoinCircle1();
				JoinCircle joinCircle2 = join.getJoinCircle2();
				
				Table table1 = joinCircle1.getTable();
				String tableName1 = table1.getName();
				
				Table table2 = joinCircle2.getTable();
				String tableName2 = table2.getName();
				
				Column col1 = joinCircle1.getColumn();
				Column col2 = joinCircle2.getColumn();
				
				if (i == 0) {
					tablesPart.append(tableName1 + " ");
				}
				tablesPart.append("\nJOIN ");
				tablesPart.append(tableName2);
				tablesPart.append(" ON ");
				tablesPart.append(tableName1 + "." + col1.getName());
				tablesPart.append(" = ");
				tablesPart.append(tableName2 + "." + col2.getName());
				tablesPart.append(" ");
			}
		}
		
		result.append(tablesPart);
		
		return result.toString();
	}
	
}
