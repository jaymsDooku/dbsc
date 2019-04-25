package io.jayms.dbsc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.InetAddress;
import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javafx.scene.paint.Color;

/**
 * Class full of helper methods. Declared final as it should never be inherited; static helper methods only.
 */
public final class GeneralUtils {

	/**
	 * Converts a java.awt.Color object to javafx.scene.paint.Color object.
	 * @param awtColor - Awt Color to convert.
	 * @return - JavaFX version of same color.
	 */
	public static Color awtToJavaFXColor(java.awt.Color awtColor) {
		int r = awtColor.getRed();
		int g = awtColor.getGreen();
		int b = awtColor.getBlue();
		int a = awtColor.getAlpha();
		double opacity = a / 255.0 ;
		javafx.scene.paint.Color fxColor = javafx.scene.paint.Color.rgb(r, g, b, opacity);
		return fxColor;
	}
	
	/**
	 * Converts a javafx.scene.paint.Color object to java.awt.Color object.
	 * @param fx - javafx Color to convert.
	 * @return - Awt version of same color.
	 */
	public static java.awt.Color javafxToAwtColor(Color fx) {
		java.awt.Color awtColor = new java.awt.Color((float) fx.getRed(),
                (float) fx.getGreen(),
                (float) fx.getBlue(),
                (float) fx.getOpacity());
		return awtColor;
	}
	
	/**
	 * How long to wait before timing out on address.
	 */
	private static final int TIMEOUT = 10000;
	
	/**
	 * Returns true if able to reach ip address, otherwise returns false. 
	 * @param address - address to check.
	 * @return - Returns whether the address is reachable.
	 */
	public static boolean isReachable(String address) {
		try {
			return InetAddress.getByName(address).isReachable(TIMEOUT);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Convert XML string to org.w3c.dom.Document.
	 * @param str - the XML in string form to convert.
	 * @return - the converted XML document.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document toXMLDocument(String str) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document document = docBuilder.parse(new InputSource(new StringReader(str)));
		return document;
	}
	
	/**
	 * Converts a CLOB retrieved from a SQL database to its string form.
	 * @param data
	 * @return - Returns the converted string form of CLOB.
	 */
	public static String clobToString(java.sql.Clob data)
	{
	    final StringBuilder sb = new StringBuilder();

	    try
	    {
	        final Reader         reader = data.getCharacterStream();
	        final BufferedReader br     = new BufferedReader(reader);

	        int b;
	        while(-1 != (b = br.read()))
	        {
	            sb.append((char)b);
	        }

	        br.close();
	    }
	    catch (SQLException | IOException e)
	    {
	    	e.printStackTrace();
	    }

	    return sb.toString();
	}
}
