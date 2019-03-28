package io.jayms.dbsc.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import javafx.scene.paint.Color;

public class GeneralUtils {

	public static Color awtToJavaFXColor(java.awt.Color awtColor) {
		int r = awtColor.getRed();
		int g = awtColor.getGreen();
		int b = awtColor.getBlue();
		int a = awtColor.getAlpha();
		double opacity = a / 255.0 ;
		javafx.scene.paint.Color fxColor = javafx.scene.paint.Color.rgb(r, g, b, opacity);
		return fxColor;
	}
	
	public static java.awt.Color javafxToAwtColor(Color fx) {
		java.awt.Color awtColor = new java.awt.Color((float) fx.getRed(),
                (float) fx.getGreen(),
                (float) fx.getBlue(),
                (float) fx.getOpacity());
		return awtColor;
	}
	
	public static boolean ping(String address, int port) {
		SocketAddress sockAddr = new InetSocketAddress(address, port);
		
		Socket socket = new Socket();
		boolean online = true;
		
		try {
			socket.connect(sockAddr, 1000);
		} catch (IOException ioException) {
			online = false;
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return online;
	}
}
