package de.hsrm.mi.swt_project.demo.scripting;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt_project.demo.collision.Collidable;
import de.hsrm.mi.swt_project.demo.controls.Direction;
import de.hsrm.mi.swt_project.demo.controls.Orientation;
import de.hsrm.mi.swt_project.demo.editor.placeableobjects.TrafficLight;
import de.hsrm.mi.swt_project.demo.editor.tiles.Tile;
import de.hsrm.mi.swt_project.demo.movables.MotorizedObject;
import de.hsrm.mi.swt_project.demo.movables.MoveableObject;
import de.hsrm.mi.swt_project.demo.objecthandling.TrafficLightSingleTon;
import de.hsrm.mi.swt_project.demo.objecthandling.TrafficLightState;
import de.hsrm.mi.swt_project.demo.util.MathHelpers;

/**
 * This class is a facade for a moveable object.
 * It is used to provide a more restricted and
 * higher level API that can be used when providing
 * scripts for moveable objects.
 * 
 * @author Sascha Scheid
 * @author Finn Schindel
 * @author Timothy Doukhin
 */
public class MoveableFacade {

    Logger logger = LoggerFactory.getLogger(getClass());

    protected static final float ACCELERATION_DELTA = 0.1f;

    private TrafficLightSingleTon trafficLightSingleTon = TrafficLightSingleTon.getInstance();

    protected MoveableObject moveable;
    protected ScriptContext context;

    /**
     * Static factory method for MoveableFacade.
     * 
     * @param moveable Moveable that will be proxied
     * @param context Context that can be provided
     * @return new instance of MoveableFacade 
     */
    public static MoveableFacade createFor(MoveableObject moveableObject, ScriptContext context) {
        return new MoveableFacade(moveableObject, context);
    }

    /**
     * Creates a new MoveableFacade.
     * 
     * @param moveable Moveable that will be controlled
     * @param context Context that can be provided
     */
    protected MoveableFacade(MoveableObject moveable, ScriptContext context) {
        this.moveable = moveable;
        this.context = context;
    }

    public float currentVelocity(){
        return moveable.getCurrentVelocity();
    }

    public Tile getFrontTile(){
        Tile[][] mapContext = context.provideMapContext();
        int pos = mapContext.length / 2;

        return mapContext[pos + 1][pos];
    }

    public boolean getFrontTrafficLight(){
        float xOnTile = moveable.getXPos() - (int) moveable.getXPos();
        float yOnTile = moveable.getYPos() - (int) moveable.getYPos();
        if (getFrontTile() == null) {
            return true;
        }
        if(getFrontTile().getPlacedObject() instanceof TrafficLight){
            switch (moveable.getOrientation()) {
                case NORTH:
                    if(yOnTile > 0.7f){
                        return isTrafficLightRed();
                    }
                    break;
                case SOUTH:
                    if(yOnTile < 0.3f){
                        return isTrafficLightRed(); 
                    }
                    break;
                case WEST:
                    if(xOnTile < 0.3f){
                        return isTrafficLightRed();
                    }
                    break;
                case EAST:
                    if(xOnTile > 0.7f){
                        return isTrafficLightRed();
                    }
                    break;
                default:
                    break;
            }
        }
        return true;


    }

    private boolean isTrafficLightRed(){
        TrafficLightState trafficLightState = trafficLightSingleTon.getTrafficLightState();
        Orientation orientation = moveable.getOrientation();
        if(trafficLightState.equals(TrafficLightState.NORTHSOUTH) && (orientation.equals(Orientation.NORTH) || orientation.equals(Orientation.SOUTH))){
            return true;
        } else if(trafficLightState.equals(TrafficLightState.EASTWEST) && (orientation.equals(Orientation.EAST) || orientation.equals(Orientation.WEST))){
            return true;
        } 
        return false;
    }

    /**
     * Turns the controlled moveable to the left.
     */
    public void turnLeft() {
        if(moveable instanceof MotorizedObject){
            moveable.setNpcDirection(Direction.LEFT);
        } else {
            this.moveable.turn(Direction.LEFT);
        }
    }

    /**
     * Turns the controlled moveable to the right.
     */
    public void turnRight() {
        if(moveable instanceof MotorizedObject){
            moveable.setNpcDirection(Direction.RIGHT);
        } else {
            this.moveable.turn(Direction.RIGHT);
        }
    }

    /**
     * Increases velocity of the controlled moveable
     * by 10% of its max velocity.
     */
    public void accelerate() {
        float newVelocity = this.moveable.getCurrentVelocity() + ACCELERATION_DELTA;
        this.moveable.setCurrentVelocity(newVelocity);
    }

    /**
     * Decreases velocity of the controlled moveable
     * by 10% of its max velocity.
     */
    public void brake() {
        float newVelocity = this.moveable.getCurrentVelocity() - ACCELERATION_DELTA;
        this.moveable.setCurrentVelocity(newVelocity);
    }

    /**
     * Sets velocity of the controlled moveable to 0.
     */
    public void emergencyBrake() {
        this.moveable.setCurrentVelocity(0);
    }

    /**
     * Provides map context which is all tiles within
     * a tile-radius of {@value #ScriptContext.LOOK_AHEAD}.
     * 
     * @return 2D array containing information of surrounding tiles.
     */
    public Tile[][] surroundingTiles() {
        return this.context.provideMapContext();
    }

    /**
     * Provides context about all other moveables
     * within a tile-radius of {@value #ScriptContext.LOOK_AHEAD}.
     * 
     * @return List containing all nearby moveables.
     */
    public List<MoveableObject> nearbyRoadUsers() {
        return this.context.provideOtherMoveablesContext();
    }

    /**
     * Calculates distance to another collidable.
     * 
     * @param other Collidable to get the distance to
     * @return Distance to the other collidable.
     */
    public float distanceTo(Collidable other) {        

        float thisXPos = this.moveable.getXPos();
        float thisYPos = this.moveable.getYPos();

        float otherXPos = other.getXPos();
        float otherYPos = other.getYPos();

        return MathHelpers.euclideanDistance(thisXPos, thisYPos, otherXPos, otherYPos);
    }    
    
}
