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
		out.write("P6 512 512 255 ");
		
		Vector g = new Vector(-6, -16, 0).normalize();
		Vector a = new Vector(0, 0, 1).cross(g).normalize().scale(.002f);
		Vector b = g.cross(a).normalize().scale(.002f);
		Vector c = a.add(b).scale(-256).add(g);
		
		for (int y = 512; y-- != 0;) {
			for (int x = 512; x-- != 0;) {
				Vector p = new Vector(13, 13, 13);
				
				for (int r = 64; r-- != 0;) {
					Vector t = a.scale(R() - .5f).scale(99).add(b.scale(R() - .5f).scale(99));
					
					p = sample(
						new Vector(17, 16, 8).add(t), 
						t.scale(-1)
						 .add(
							a.scale(R() + x)
							 .add(b.scale(y + R()))
						     .add(c)
						     .scale(16)
						 ).normalize()
					).scale(3.5f).add(p);
				}
				
				out.write(String.format("%c%c%c", (int) p.x, (int) p.y, (int) p.z));
			}
		}
	}
	
	Vector sample(Vector o, Vector d) {
		S s = trace(o, d);
		int m = s.m;
		float t = s.t;
		Vector n = s.n;
		
		
		if (m == 0) {
			return new Vector(.7f, .6f, 1).scale((float) Math.pow(1.0f - d.z, 4));
		}
		
		Vector h = o.add(d.scale(t));
		Vector l = new Vector(9 + R(), 9 + R(), 16).add(h.scale(-1)).normalize();
		Vector r = d.add(n.scale(n.dot(d) * -2.0f));
		
		float b = l.dot(n);
		
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
		
		float p = (float) Math.pow(l.dot(r) * (b > 0 ? 1.0f : 0.0f), 99);
				
		if (m == 1) {
			h = h.scale(.2f);
			
			if ((int) (ceil(h.x) + ceil(h.y)) % 2 == 1) {
				return new Vector(3, 1, 1).scale(b * .2f + .1f);
			} else { 
				return new Vector(3, 3, 3).scale(b * .2f + .1f);
			}
		}
		
		if (m != 2) {
			System.out.println("m = " + m);
		}
		
		return new Vector(p, p, p).add(sample(h, r).scale(.5f));
	}
	
	S trace(Vector o, Vector d) {
		Vector n = new Vector();
		float t = (float) 1e9;
		int m = 0;
		float p = -o.z / d.z;
		
		if (.01 < p) {
			t = p;
			n = new Vector(0, 0, 1);
			m = 1;
		}
		
		for (int k = 19; k-- != 0;) {
			for (int j = 9; j-- != 0;) {
				if ((G[j] & (1 << k)) != 0) {
					Vector vp = o.add(new Vector(-k, 0, -j - 4));
					float b = vp.dot(d);
					float c = vp.dot(vp) - 1;
					float q = b * b - c;
					
					if (q > 0) {
						float s = -b - (float) Math.sqrt(q);
						
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
	
	float R() {
		return (float) Math.random();
	}
	
	float ceil(float n) {
		return (float) Math.ceil(n);
	}
	
	class S {
		public int m;
		public float t;
		public Vector n;
		
		public S(int m, float t, Vector n) {
			this.m = m;
			this.t = t;
			this.n = n;
		}
	}

}
