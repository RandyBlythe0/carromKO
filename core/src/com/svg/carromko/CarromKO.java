package com.svg.carromko;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;




public class CarromKO extends ApplicationAdapter implements InputProcessor{
	SpriteBatch batch;
//	Texture img;
	World world;
	OrthographicCamera camera;
    ShapeRenderer shapeRenderer;
    Box2DDebugRenderer debugRenderer;
    ArrayList<Body> body;
    ArrayList<Body> wall;
    Body hitBody;
    Body border;
//    Color boxcolor;
    
    float w,h;
    final float PIXELS_TO_METERS = 100f;
    boolean dragging=false;
    Vector3 dragStart,dragEnd;
    BitmapFont font;
    
	@Override
	public void create () {
		batch = new SpriteBatch();
		font=new BitmapFont();
//		img = new Texture("badlogic.jpg");
		w = Gdx.graphics.getWidth()/PIXELS_TO_METERS;
        h = Gdx.graphics.getHeight()/PIXELS_TO_METERS;
		
		world = new World(new Vector2(0f, 0f),true);
        
        Gdx.input.setInputProcessor(this);
        
        camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.
                getHeight());        
        shapeRenderer = new ShapeRenderer();
        debugRenderer = new Box2DDebugRenderer();
//        boxcolor = new Color(Color.BLACK);
        
        dragStart=new Vector3();
        dragEnd=new Vector3();
        body = new ArrayList<Body>();
        wall = new ArrayList<Body>();
        
//        buildMapWall(0,0,5.5f,3f,-5.5f,3f);
//        buildMapWall(0,0,5.5f,-3f,-5.5f,-3f);
//        buildMapWall(0,0,5.5f,3f,5.5f,-3f);
//        buildMapWall(0,0,-5.5f,3f,-5.5f,-3f);
//        spawnBody(1,1,0.2f,1,0.5f);
//        spawnBody(0,0,0.2f,1,0.5f);
        
        FileHandle fd = Gdx.files.internal("map001.xml");
        XmlReader xmlreader=new XmlReader();
        Element root=xmlreader.parse(fd);
        //dbug
        
        for(int i=0;i<root.getChildCount();i++) {
        	Element tmp = root.getChild(i);
        		for(int j=0;j<tmp.getChildCount();j++) {
        			if(tmp.hasAttribute("radius")) {
        				float rad = tmp.getFloat("radius");
        				float den = tmp.getFloat("density");
        				float res = tmp.getFloat("restitution");
        				System.out.println("rad "+rad+" den "+den+" res "+res);
        				Element tmp1 = tmp.getChild(j);
        				
        				float posx = tmp1.getFloat("posx");
        				float posy = tmp1.getFloat("posy");
        				
        				System.out.println("posx "+posx+" posy "+posy);
        				spawnBody(posx,posy,rad,den,res);
        			}
        			else {
        				Element tmp1 = tmp.getChild(j);
        				float posx = tmp1.getFloat("posx");
        				float posy = tmp1.getFloat("posy");
        				float edgex1 = tmp1.getFloat("edgex1");
        				float edgey1 = tmp1.getFloat("edgey1");
        				float edgex2 = tmp1.getFloat("edgex2");
        				float edgey2 = tmp1.getFloat("edgey2");
        				
        				buildMapWall(posx,posy,edgex1,edgey1,edgex2,edgey2);
        			}
        			
        		}
        }
        
	}

	@Override
	public void render () {
		camera.update();
        //camera.zoom+=0.05f;
        //camera.translate(0, 0, 0);
        //camera.position.x+=1;
        //camera.zoom+=0.05f;
        //camerax=1;
        // Step the physics simulation forward at a rate of 60hz
        world.step(1f/60f, 6, 2);
        
		Gdx.gl.glClearColor(1, 1, 1, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	    
	    debugRenderer.render(world, camera.combined.cpy().scale(PIXELS_TO_METERS, 
                PIXELS_TO_METERS, 0));
	    batch.setProjectionMatrix(camera.combined);
		batch.begin();
//		batch.draw(img, 0, 0);
		font.draw(batch, "Data:" + dragStart.toString() + " | " + dragEnd.toString() , 0, 0);
		font.draw(batch, "Data:" + body.get(0).getPosition().toString() , 0, 20);
		font.draw(batch, "Data:" + body.get(1).getPosition().toString() , 0, 40);
		batch.end();
	    
//	    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setProjectionMatrix(camera.combined);
//        shapeRenderer.setColor(Color.BLACK);
//        shapeRenderer.circle(30, 30, 10);
//        shapeRenderer.end();
	}
	
	@Override
	public void dispose () {
//		batch.dispose();
//		img.dispose();
	}

	
	

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}
	
	Vector3 testPoint = new Vector3();
	QueryCallback callback = new QueryCallback() {
		@Override
		public boolean reportFixture (Fixture fixture) {
			// if the hit fixture's body is the ground body
			// we ignore it
//			if (fixture.getBody() == groundBody) return true;

			// if the hit point is inside the fixture of the body
			// we report it
			if (fixture.testPoint(testPoint.x, testPoint.y)) {
				hitBody = fixture.getBody();
				return false;
			} else
				return true;
		}
	};
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		
		if(dragging) { 
//			body.get(0).applyTorque(2f, true);
			dragging=false;
			dragEnd.set(screenX,screenY,0);
			camera.unproject(dragEnd).scl(1/PIXELS_TO_METERS);
			testPoint.set(dragStart.x,dragStart.y,0);
			
			hitBody = null;
			world.QueryAABB(callback, testPoint.x - 0.1f, testPoint.y - 0.1f, testPoint.x + 0.1f, testPoint.y + 0.1f);
			
			if(hitBody!=null) {
				hitBody.applyForceToCenter(20*(dragStart.x-dragEnd.x),20*(dragStart.y-dragEnd.y), true);
			}
//			body.get(0).applyForceToCenter(10*(dragStart.x-dragEnd.x)/PIXELS_TO_METERS,10*(dragStart.y-dragEnd.y)/PIXELS_TO_METERS, true);
//			body.get(0).applyForceToCenter(-2, -2,true);
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		if(!dragging) {
		dragStart.set(screenX,screenY,0);
		camera.unproject(dragStart).scl(1/PIXELS_TO_METERS);
		dragging = true;
//		body.get(0).setTransform(screenX/PIXELS_TO_METERS, -screenY/PIXELS_TO_METERS, 0);
//		for(int i=0;i<body.size();i++) {body.get(i).setSleepingAllowed(false);}
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		
		camera.zoom+=amount;
		return false;
	}
	
	public void spawnBody(float x,float y, float radius, float dens, float rest) {
		//int bodyHeight = 30;
		BodyDef bodyDef = new BodyDef();
		//applyInitialForce=true;
    	bodyDef.type = BodyDef.BodyType.DynamicBody;
    	bodyDef.position.set(x,y);
        //shape defination
//        PolygonShape shapeDomino = new PolygonShape();
//        shapeDomino.setAsBox(bodyHeight/4 , bodyHeight );
        //fixturedef
    	CircleShape shapeBody = new CircleShape();
//    	shapeBody.setPosition(x,y);
    	shapeBody.setRadius(radius);
        FixtureDef fixtureDefDomino = new FixtureDef();
        fixtureDefDomino.shape = shapeBody;
        fixtureDefDomino.density = dens;
        fixtureDefDomino.restitution = rest;
        
        
        
        body.add(world.createBody(bodyDef));
        body.get(body.size()-1).setUserData("Body"+body.size());
//        body.get(body.size()-1).setSleepingAllowed(false);
        body.get(body.size()-1).createFixture(fixtureDefDomino);
        body.get(body.size()-1).setLinearDamping(1.5f);
        body.get(body.size()-1).setAngularDamping(1.5f);
        //createbody
//        dominos[i] = world.createBody(bodyDefDomino);
//        dominos[i].setUserData("Domino"+i);
//        dominos[i].createFixture(fixtureDefDomino);
        shapeBody.dispose();
        
        //if(dominos.size() > 20) { world.destroyBody(dominos.get(0)); dominos.remove(0);}
        
	}
	
	public void buildMapWall(float x1,float y1, float x2, float y2, float x3, float y3) {
		BodyDef bodyDef3 = new BodyDef();
	      bodyDef3.type = BodyDef.BodyType.StaticBody;
	      
	      bodyDef3.position.set(x1,y1);
	      FixtureDef fixtureDef3 = new FixtureDef();

	      EdgeShape edgeShape = new EdgeShape();
	      edgeShape.set(x2,y2,x3,y3);
	      fixtureDef3.shape = edgeShape;
	      wall.add(world.createBody(bodyDef3));
	      wall.get(wall.size()-1).createFixture(fixtureDef3);
//	      bodyBottomEdgeScreen = world.createBody(bodyDef3);
//	      bodyBottomEdgeScreen.createFixture(fixtureDef3);
	      edgeShape.dispose();
	}
	
	public void resize(int width, int height) {
	    camera.setToOrtho(false);
	    camera.translate(-width/2,-height/2);
	}
}

