package com.svgames.carromko;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
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
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;



public class CarromKO extends ApplicationAdapter implements InputProcessor{
	SpriteBatch batch;
//	Texture img;
	World world;
	OrthographicCamera camera;
    ShapeRenderer shapeRenderer;
    Box2DDebugRenderer debugRenderer;
    ArrayList<Body> body;
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
        spawnBody(1,1,0.2f);
        spawnBody(0,0,0.2f);
        
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

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		
		if(dragging) { 
//			body.get(0).applyTorque(2f, true);
			dragging=false;
			dragEnd.set(screenX,screenY,0);
			camera.unproject(dragEnd);
			body.get(0).applyForceToCenter(10*(dragStart.x-dragEnd.x)/PIXELS_TO_METERS,10*(dragStart.y-dragEnd.y)/PIXELS_TO_METERS, true);
//			body.get(0).applyForceToCenter(-2, -2,true);
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		if(!dragging) {
		dragStart.set(screenX,screenY,0);
		camera.unproject(dragStart);
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
		
		return false;
	}
	
	public void spawnBody(float x,float y, float radius) {
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
        fixtureDefDomino.density = 1f;
        fixtureDefDomino.restitution = 0.15f;
        
        
        body.add(world.createBody(bodyDef));
        body.get(body.size()-1).setUserData("Body"+body.size());
        body.get(body.size()-1).setSleepingAllowed(false);
        body.get(body.size()-1).createFixture(fixtureDefDomino);
        body.get(body.size()-1).setLinearDamping(2f);
        //createbody
//        dominos[i] = world.createBody(bodyDefDomino);
//        dominos[i].setUserData("Domino"+i);
//        dominos[i].createFixture(fixtureDefDomino);
        shapeBody.dispose();
        
        //if(dominos.size() > 20) { world.destroyBody(dominos.get(0)); dominos.remove(0);}
        
	}
	
	public void buildMap() {
		BodyDef bodyDef= new BodyDef();
		
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(-300,-300);
	}
	
	public void resize(int width, int height) {
	    camera.setToOrtho(false);
	    camera.translate(-width/2,-height/2);
	}
}

