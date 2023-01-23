package de.hsrm.mi.swt_project.demo.railingsystem;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt_project.demo.controls.Direction;
import de.hsrm.mi.swt_project.demo.controls.Orientation;
import de.hsrm.mi.swt_project.demo.editor.tiles.Tile;
import de.hsrm.mi.swt_project.demo.editor.tiles.Tiletype;
import de.hsrm.mi.swt_project.demo.movables.MoveableObject;

public class RailingBehaviour {

    public static final float UP_ALIGNMENT_OFFSET = 0.7f;
    public static final float RIGHT_ALIGNMENT_OFFSET = 0.7f;
    public static final float DOWN_ALIGNMENT_OFFSET = 0.3f;
    public static final float LEFT_ALIGNMENT_OFFSET = 0.3f;

    public static final float APPROXIMATION_STEP = 0.002f;

    private Map<String, RailingMemoryCell> railingMemory = new HashMap<>();

    Logger logger = LoggerFactory.getLogger(getClass());
    Direction testDirectionDeleteLater = (int)Math.random()*100 < 50 ? Direction.LEFT : Direction.RIGHT; 

    public void railCoordinates(String key, MoveableObject moveable, Tile tile, Direction dir) {
        if(railingMemory.containsKey(key)){
            RailingMemoryCell memoryCell = railingMemory.get(key);
            if(memoryCell.leftTile((int)moveable.getXPos(), (int)moveable.getYPos())){
                memoryCell.setAlreadyTurned(false);
                railingMemory.put(key, memoryCell);
                testDirectionDeleteLater = (int)(Math.random()*100) < 50 ? Direction.LEFT : Direction.RIGHT;
                logger.info(""+testDirectionDeleteLater);
            }
        } else {
            RailingMemoryCell memoryCell = new RailingMemoryCell((int)moveable.getXPos(), (int)moveable.getYPos());
            railingMemory.put(key, memoryCell);
        }

        float movement = moveable.getCurrentVelocity() * moveable.getMaxVelocity();
        if (tile.getType().equals(Tiletype.STREET_STRAIGHT)) {
            straightBehaviour(moveable, tile, movement);

        } else if (tile.getType().equals(Tiletype.STREET_CURVE)) {
            curveBehaviour(key, moveable, tile, movement);

        } else if(tile.getType().equals(Tiletype.STREET_CROSS)){
            crossBehaviour(key, moveable, tile, movement, testDirectionDeleteLater);

        } 

    }

    public void crossBehaviour(String key, MoveableObject moveable, Tile tile, float movement, Direction dir){
        Tile straightConversionTile = Tiletype.STREET_STRAIGHT.createTile();
        Tile curveConversionTile = Tiletype.STREET_CURVE.createTile();
        Orientation orientation = moveable.getOrientation();
        boolean alreadyTurned = railingMemory.get(key).isAlreadyTurned();
        switch (dir) {
            case LEFT:
                if(alreadyTurned){
                    straightConversionTile.setOrientation(orientation);
                    straightBehaviour(moveable, straightConversionTile, movement);
                } else{
                    curveConversionTile.setOrientation(orientation);
                    curveBehaviour(key, moveable, curveConversionTile, movement);
                }
                break;
            case RIGHT:
                if(alreadyTurned){
                    straightConversionTile.setOrientation(orientation);
                    straightBehaviour(moveable, straightConversionTile, movement);
                } else{
                    curveConversionTile.setOrientation(orientation.prev().prev());
                    curveBehaviour(key, moveable, curveConversionTile, movement);
                }
                break;
            default:
                straightConversionTile.setOrientation(orientation);
                straightBehaviour(moveable, straightConversionTile, movement);
                break;
        }
    }

    public void straightBehaviour(MoveableObject moveable, Tile tile, float movement) {

        float xPos = moveable.getXPos();
        float yPos = moveable.getYPos();

        Orientation orientation = moveable.getOrientation();

        if (tile.getOrientation() == Orientation.NORTH || tile.getOrientation() == Orientation.SOUTH) {

            if (orientation == Orientation.NORTH) {

                moveable.setXPos((int) xPos + RIGHT_ALIGNMENT_OFFSET);
                moveable.setYPos(yPos + movement);

            } else if (orientation == Orientation.SOUTH) {

                moveable.setXPos((int) xPos + LEFT_ALIGNMENT_OFFSET);
                moveable.setYPos(yPos - movement);

            }

        } else {

            if (orientation == Orientation.EAST) {

                moveable.setYPos((int) yPos + DOWN_ALIGNMENT_OFFSET);
                moveable.setXPos(xPos + movement);

            } else if (orientation == Orientation.WEST) {

                moveable.setYPos((int) yPos + UP_ALIGNMENT_OFFSET);
                moveable.setXPos(xPos - movement);

            }
        }
    }  

    public void curveBehaviour(String key, MoveableObject moveable, Tile tile, float movement) {

        float xPos = moveable.getXPos();
        float yPos = moveable.getYPos();
        Orientation orientation = moveable.getOrientation();

        RailingMemoryCell memoryCell = railingMemory.get(key);

        if (tile.getOrientation() == Orientation.NORTH) {

            if (orientation == Orientation.NORTH) {

                moveable.setXPos((int) xPos + RIGHT_ALIGNMENT_OFFSET);

                if (yPos + movement > ((int) yPos + UP_ALIGNMENT_OFFSET)) {
                    moveable.setYPos((int) yPos + UP_ALIGNMENT_OFFSET);
                    doCurveAproximationStep(key, memoryCell, moveable, tile, Direction.LEFT);

                } else {
                    moveable.setYPos(yPos + movement);
                }

            } else if (orientation == Orientation.EAST) {

                moveable.setYPos((int) yPos + DOWN_ALIGNMENT_OFFSET);

                if (xPos + movement > (int) xPos + LEFT_ALIGNMENT_OFFSET) {

                    moveable.setXPos((int) xPos + LEFT_ALIGNMENT_OFFSET);
                    doCurveAproximationStep(key, memoryCell, moveable, tile, Direction.RIGHT);

                } else {
                    moveable.setXPos(xPos + movement);
                }

            } else if (orientation == Orientation.SOUTH) {

                moveable.setXPos((int) xPos + LEFT_ALIGNMENT_OFFSET);
                moveable.setYPos(yPos - movement);

            } else if (orientation == Orientation.WEST) {

                moveable.setYPos((int) yPos + UP_ALIGNMENT_OFFSET);
                moveable.setXPos(xPos - movement);

            }

        } else if (tile.getOrientation() == Orientation.EAST) {

            if (orientation == Orientation.NORTH) {

                moveable.setXPos((int) xPos + RIGHT_ALIGNMENT_OFFSET);
                moveable.setYPos(yPos + movement);

            } else if (orientation == Orientation.EAST) {

                moveable.setYPos((int) yPos + DOWN_ALIGNMENT_OFFSET);

                if (xPos + movement > (int) xPos + RIGHT_ALIGNMENT_OFFSET) {

                    moveable.setXPos((int) xPos + RIGHT_ALIGNMENT_OFFSET);
                    doCurveAproximationStep(key, memoryCell, moveable, tile, Direction.LEFT);

                } else {
                    moveable.setXPos(xPos + movement);
                }

            } else if (orientation == Orientation.SOUTH) {

                moveable.setXPos((int) xPos + LEFT_ALIGNMENT_OFFSET);

                if (yPos - movement < (int) yPos + UP_ALIGNMENT_OFFSET) {

                    moveable.setYPos((int) yPos + UP_ALIGNMENT_OFFSET);
                    doCurveAproximationStep(key, memoryCell, moveable, tile, Direction.RIGHT);

                } else {
                    moveable.setYPos(yPos - movement);
                }

            } else if (orientation == Orientation.WEST) {

                moveable.setYPos((int) yPos + UP_ALIGNMENT_OFFSET);
                moveable.setXPos(xPos - movement);

            }

        } else if (tile.getOrientation() == Orientation.SOUTH) {

            if (orientation == Orientation.NORTH) {

                moveable.setXPos((int) xPos + RIGHT_ALIGNMENT_OFFSET);
                moveable.setYPos(yPos + movement);

            } else if (orientation == Orientation.EAST) {

                moveable.setYPos((int) yPos + DOWN_ALIGNMENT_OFFSET);
                moveable.setXPos(xPos + movement);

            } else if (orientation == Orientation.SOUTH) {

                moveable.setXPos((int) xPos + LEFT_ALIGNMENT_OFFSET);

                if (yPos - movement < (int) yPos + DOWN_ALIGNMENT_OFFSET) {

                    moveable.setYPos((int) yPos + DOWN_ALIGNMENT_OFFSET);
                    doCurveAproximationStep(key, memoryCell, moveable, tile, Direction.LEFT);

                } else {
                    moveable.setYPos(yPos - movement);
                }

            } else if (orientation == Orientation.WEST) {

                moveable.setYPos((int) yPos + UP_ALIGNMENT_OFFSET);

                if (xPos - movement < (int) xPos + RIGHT_ALIGNMENT_OFFSET) {

                    moveable.setYPos((int) yPos + UP_ALIGNMENT_OFFSET);
                    doCurveAproximationStep(key, memoryCell, moveable, tile, Direction.RIGHT);

                } else {
                    moveable.setXPos(xPos - movement);
                }
            }

        } else if (tile.getOrientation() == Orientation.WEST) {

            if (orientation == Orientation.NORTH) {

                moveable.setXPos((int) xPos + RIGHT_ALIGNMENT_OFFSET);

                if (yPos + movement > (int) yPos + DOWN_ALIGNMENT_OFFSET) {

                    moveable.setYPos((int) yPos + DOWN_ALIGNMENT_OFFSET);
                    doCurveAproximationStep(key, memoryCell, moveable, tile, Direction.RIGHT);

                } else {
                    moveable.setYPos(yPos + movement);
                }

            } else if (orientation == Orientation.EAST) {

                moveable.setYPos((int) yPos + DOWN_ALIGNMENT_OFFSET);
                moveable.setXPos(xPos + movement);

            } else if (orientation == Orientation.SOUTH) {

                moveable.setXPos((int) xPos + LEFT_ALIGNMENT_OFFSET);
                moveable.setYPos(yPos - movement);

            } else if (orientation == Orientation.WEST) {

                moveable.setYPos((int) yPos + UP_ALIGNMENT_OFFSET);

                if (xPos - movement < (int) xPos + LEFT_ALIGNMENT_OFFSET) {

                    moveable.setYPos((int) yPos + UP_ALIGNMENT_OFFSET);
                    doCurveAproximationStep(key, memoryCell, moveable, tile, Direction.LEFT);

                } else {
                    moveable.setXPos(xPos - movement);
                }
            }
        }
    }  

    private void doCurveAproximationStep(String key, RailingMemoryCell memoryCell, MoveableObject moveable, Tile tile, Direction d){
        moveable.turn(d);
        memoryCell.setAlreadyTurned(true);
        railingMemory.put(key, memoryCell);
        curveBehaviour(key, moveable, tile, APPROXIMATION_STEP);
    }

}
