/**
 * With great help from Fabien Sanglard
 * 
 * http://fabiensanglard.net/rayTracing_back_of_business_card/index.php
 * 
 */
package com.jonjonsonjr.raytracer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RayTracer {

	int G[]= {247570, 280596, 280600, 249748, 18578, 18577, 231184, 16, 16};
	BufferedWriter out;
	
	public void run() throws IOException {
		out = new BufferedWriter(new FileWriter(new File("aek.ppm")));
		out.write("P6 512 512 255 "); // The PPM Header is issued
		
		Vector g = new Vector(-6, -16, 0).normalize(); 					 // Camera direction
		Vector a = new Vector(0, 0, 1).cross(g).normalize().scale(.002); // Camera up vector...Seem Z is pointing up :/ WTF !
		Vector b = g.cross(a).normalize().scale(.002); 					 // The right vector, obtained via traditional cross-product
		Vector c = a.add(b).scale(-256).add(g); 						 // WTF ? See https://news.ycombinator.com/item?id=6425965 for more.
		
		for (int y = 511; y >= 0; y--) {		// For each column
			for (int x = 511; x >= 0; x--) {	// For each pixel in a line
				
				//Reuse the vector class to store not XYZ but a RGB pixel color
				Vector p = new Vector(13, 13, 13);
				
				//Cast 64 rays per pixel (For blur (stochastic sampling) and soft-shadows.
				for (int r = 31; r >= 0; r--) {
					
					// The delta to apply to the origin of the view (For Depth of View blur).
					Vector t = a.scale(R() - .5).scale(99).add(b.scale(R() - .5).scale(99));
					
					// Set the camera focal point v(17,16,8) and Cast the ray 
		            // Accumulate the color returned in the p variable
					p = sample(
						new Vector(17, 16, 8).add(t), // Ray Direction with random deltas for stochastic sampling
						t.scale(-1)
						 .add(
							a.scale(R() + x)
							 .add(b.scale(y + R()))
						     .add(c)
						     .scale(16)
						 ).normalize()
					).scale(3.5)	// 3.5 for global illumination (I think)
					 .add(p); 		// +p for color accumulation
				}

				out.write(String.format("%c%c%c", (int) p.x, (int) p.y, (int) p.z));
			}
		}
	}
	
	// (S)ample the world and return the pixel color for
	// a ray passing by point o (Origin) and d (Direction)
	Vector sample(Vector o, Vector d) {
		S s = trace(o, d);
		double t = s.t;
		Vector n = s.n;
		
		//Search for an intersection ray Vs World.
		int m = s.m;
		
		
		if (m == 0) {
			//No sphere found and the ray goes upward: Generate a sky color
			return new Vector(.7, .6, 1).scale(pow(1 - d.z, 4));
		}
		
		//A sphere was maybe hit.
		
		Vector h = o.add(d.scale(t)); 											  // h = intersection coordinate
		Vector l = new Vector(9 + R(), 9 + R(), 16).add(h.scale(-1)).normalize(); // 'l' = direction to light (with random delta for soft-shadows).
		Vector r = d.add(n.scale(n.dot(d) * -2));								  // r = The half-vector
		
		double b = l.dot(n); //Calculated the lambertian factor
		
		//Calculate illumination factor (lambertian coefficient > 0 or in shadow)?
		if (b < 0) {
			b = 0;
		} else {
			S res = trace(h, l);
			t = res.t;
			n = res.n;
			
			if (res.m != 0) {
				b = 0;
			}
		}
			
		double p = pow(l.dot(r) * (b > 0 ? 1 : 0), 99); // Calculate the color 'p' with diffuse and specular component 
				
		if (m == 1) {
			//No sphere was hit and the ray was going downward: Generate a floor color
			h = h.scale(.2);
			
			if ((((int) (ceil(h.x) + ceil(h.y))) & 1) != 0) {
				return new Vector(3, 1, 1).scale(b * .2 + .1);
			} else { 
				return new Vector(3, 3, 3).scale(b * .2 + .1);
			}
		}
		
		return new Vector(p, p, p).add(sample(h, r).scale(.5));
	}
	
	S trace(Vector o, Vector d) {
		Vector n = new Vector();
		double t = (double) 1e9;
		int m = 0;
		double p = -o.z / d.z;
		
		if (.01 < p) {
			t = p;
			n = new Vector(0, 0, 1);
			m = 1;
		}
		
		for (int k = 18; k >= 0; k--) {
			for (int j = 8; j >= 0; j--) {
				if ((G[j] & 1 << k) != 0) { // Draw a ball here
					Vector vp = o.add(new Vector(-k, 0, -j - 4));
					double b = vp.dot(d);
					double c = vp.dot(vp) - 1;
					double q = b * b - c;
					
					if (q > 0) {
						double s = -b - sqrt(q);
						
						if (s < t && s > .01) {
							t = s;
							n = vp.add(d.scale(t)).normalize();
							m = 2;
						}
					}
				}
			}
		}
		
		return new S(m, t, n);
	}
	
	double R() {
		return (double) Math.random();
	}
	
	double ceil(double n) {
		return (double) Math.ceil(n);
	}
	
	double pow(double b, int e) {
		return (double) Math.pow(b, e);
	}
	
	double sqrt(double n) {
		return (double) Math.sqrt(n);
	}
	
	class S {
		public int m;
		public double t;
		public Vector n;
		
		public S(int m, double t, Vector n) {
			this.m = m;
			this.t = t;
			this.n = n;
		}
	}

}
