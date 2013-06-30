package com.ser.assist;

import soot.Body;
import soot.Local;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.Stmt;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: gdevanla
 * Date: 6/26/13
 * Time: 6:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class SootHelper {

    private final Body body;
    public SootHelper(Body body){
        this.body = body;
    }

    public Unit getReachingDefStatement(Unit beforeUnit){

        for(Unit unit:body.getUnits()){
            List<ValueBox> valueBoxes = unit.getDefBoxes();
            System.out.println(valueBoxes);
            if ( beforeUnit.equals(unit)){
                System.out.println(beforeUnit);
                System.out.println("equals");
                System.out.println(unit);
            }
        }

        return null;
    }

}
