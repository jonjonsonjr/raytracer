package com.jonjonsonjr.raytracer;

public class Vector {
	
	public double x;
	public double y;
	public double z;
	
	public Vector() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector add(Vector v) {
		return new Vector(x + v.x, y + v.y, z + v.z);
	}
	
	public Vector scale(double s) {
		return new Vector(x * s, y * s, z * s);
	}
	
	public double dot(Vector v) {
		return x * v.x + y * v.y + z * v.z;
	}

	public Vector cross(Vector v) {
		return new Vector(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
	}
	
	public Vector normalize() {
		return this.scale(1.0 / (double) Math.sqrt(this.dot(this)));
	}
	
	public String toString() {
		return "{x: " + x + ", y: " + y + ", z: " + z + "}";
	}
}
