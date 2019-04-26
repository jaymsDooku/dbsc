package io.jayms.dbsc.qb;

import java.io.IOException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.ui.QueryBuilderUI;
import io.jayms.dbsc.util.GeneralUtils;
import io.jayms.dbsc.util.Validation;
import io.jayms.xlsx.db.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ExplainPlanButton extends Button {

	private DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
	private DBSCGraphicalUserInterface masterUI;
	private QueryBuilderUI qbUI;
	
	/**
	 * Instantiate explain button.
	 * @param masterUI
	 * @param qbUI
	 */
	public ExplainPlanButton(DBSCGraphicalUserInterface masterUI, QueryBuilderUI qbUI) {
		super("Explain Plan");		
		
		this.masterUI = masterUI;
		this.qbUI = qbUI;
		this.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
		this.onMouseClickedProperty().set((e) -> { // on clicking explain button, go through database types and call corresponding explain plan method.
			String query = qbUI.getQueryBuilderContext().generateQuery(false);
			
			DB db = qbUI.getDb();
			Database dbConn = masterUI.getDatabaseManager().getDatabaseConnection(db);
			Connection conn = dbConn.getConnection();
			switch (db.getType()) {
				case SQLITE:
					explainPlanSQLite(conn, query);
					break;
				case ORACLE:
					explainPlanOracle(conn, query);
					break;
				case SQL_SERVER:
					explainPlanSQLServer(conn, query);
					break;
				default:
					break;
			}
		});
	}
	
	private void explainPlanOracle(Connection conn, String query) {
		Statement stmt = null;
		try {
			System.out.println("query: " + query);
			stmt = conn.createStatement();
			stmt.execute("EXPLAIN PLAN SET STATEMENT_ID = 'abc' FOR " + query);
			ResultSet rs = stmt.executeQuery("SELECT dbms_xplan.build_plan_xml(statement_id => 'abc').getclobval() AS XPLAN FROM dual");
			if (rs.next()) {
				Clob xplanClob = rs.getClob("XPLAN");
				String xplanXmlStr = GeneralUtils.clobToString(xplanClob);
				System.out.println("xplanXml: " + xplanXmlStr);
				try {
					Document xplanXml = GeneralUtils.toXMLDocument(xplanXmlStr);
					Element root = xplanXml.getDocumentElement();
					NodeList operations = root.getElementsByTagName("operation");
					int opLength = operations.getLength();
					Set<OracleOperation> oracleOps = new HashSet<>();
					for (int i = 0; i < opLength; i++) {
						Element op = (Element) operations.item(i);
						String opName = op.getAttribute("name");
						Element objectEl = (Element) (op.getElementsByTagName("object").item(0));
						Element rowsEl = (Element) (op.getElementsByTagName("card").item(0));
						Element bytesEl = (Element) (op.getElementsByTagName("bytes").item(0));
						Element costEl = (Element) (op.getElementsByTagName("cost").item(0));
						Element timeEl = (Element) (op.getElementsByTagName("time").item(0));
						String object = objectEl != null ? objectEl.getTextContent() : null;
						String rowsVal = rowsEl.getTextContent();
						int rows = Integer.parseInt(rowsVal);
						long bytes = Long.parseLong(bytesEl.getTextContent());
						long cost = Long.parseLong(costEl.getTextContent());
						String timeStr = timeEl.getTextContent();
						Time time = new Time(formatter.parse(timeStr).getTime());
						OracleOperation oracleOp = new OracleOperation(opName, object, rows, bytes, cost, time);
						oracleOps.add(oracleOp);
					}
					displayOracleOperations(oracleOps);
				} catch (ParserConfigurationException | SAXException | IOException | ParseException e1) {
					e1.printStackTrace();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void displayOracleOperations(Set<OracleOperation> oracleOps) {
		TableView<OracleOperation> oracleOpTable = new TableView<OracleOperation>();
		oracleOpTable.setEditable(false);
		oracleOpTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		TableColumn opNameCol = oracleOperationTableColumn("Operation Name", "operationName");
		TableColumn objCol = oracleOperationTableColumn("Object", "object");
		TableColumn rowsCol = oracleOperationTableColumn("Rows", "rows");
		TableColumn bytesCol = oracleOperationTableColumn("Bytes", "bytes");
		TableColumn costCol = oracleOperationTableColumn("Cost", "cost");
		TableColumn timeCol = oracleOperationTableColumn("Time", "time");
		
		oracleOpTable.getColumns().addAll(opNameCol, objCol, rowsCol, bytesCol, costCol, timeCol);
		
		ObservableList<OracleOperation> oracleOpsObservable = oracleOps.stream().collect(Collectors.collectingAndThen(Collectors.toList(), l -> FXCollections.observableArrayList(l)));
		oracleOpTable.setItems(oracleOpsObservable);
		
		qbUI.updateExplainPlanTable(oracleOpTable);
	}
	
	private TableColumn oracleOperationTableColumn(String title, String fieldName) {
		TableColumn col = new TableColumn(title);
		col.setCellValueFactory(new PropertyValueFactory<OracleOperation, String>(fieldName));
		return col;
	}
	
	private void explainPlanSQLite(Connection conn, String query) {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("EXPLAIN QUERY PLAN " + query);
			
			Set<SQLiteOperation> sqliteOps = new HashSet<>();
			while (rs.next()) {
				String detail = rs.getString("detail");
				SQLiteOperation sqliteOp = new SQLiteOperation(detail);
				sqliteOps.add(sqliteOp);
			}
			displaySQLiteOperations(sqliteOps);
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void displaySQLiteOperations(Set<SQLiteOperation> sqliteOps) {
		TableView<SQLiteOperation> sqliteOpTable = new TableView<SQLiteOperation>();
		sqliteOpTable.setEditable(false);
		sqliteOpTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		TableColumn detailCol = oracleOperationTableColumn("Detail", "detail");

		sqliteOpTable.getColumns().addAll(detailCol);
		
		ObservableList<SQLiteOperation> oracleOpsObservable = sqliteOps.stream().collect(Collectors.collectingAndThen(Collectors.toList(), l -> FXCollections.observableArrayList(l)));
		sqliteOpTable.setItems(oracleOpsObservable);
		
		qbUI.updateExplainPlanTable(sqliteOpTable);
	}
	
	/**
	 * SQL Server doesn't have an obvious way of exposing their SQL plan execution through JDBC.
	 * 
	 * https://stackoverflow.com/questions/11100479/retrieve-sql-server-query-statistics-through-jdbc
	 * In order to fetch a SQL Server query plan, it would require execution code on the database-side, which is out of the scope of the specification.
	 */
	private void explainPlanSQLServer(Connection conn, String query) {
		Validation.alert("SQL Server explain query plan is unsupported.");
	}
}
