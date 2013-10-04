package com.jonjonsonjr.raytracer;

import java.io.IOException;

public class Start {
	
	public static void main(String[] args) {
		try {
			new RayTracer().run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
