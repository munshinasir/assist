package com.ser.assist.oraclefinder;

import com.sun.deploy.util.StringQuoteUtil;
import com.sun.jmx.interceptor.DefaultMBeanServerInterceptor;
import soot.*;
import soot.dava.toolkits.base.AST.structuredAnalysis.ReachingDefs;
import soot.jimple.DefinitionStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.JimpleBody;
import soot.jimple.Stmt;
import soot.jimple.internal.JimpleLocal;
import soot.options.Options;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.graph.pdg.EnhancedUnitGraph;
import soot.toolkits.scalar.SimpleLocalDefs;
import soot.toolkits.scalar.SimpleLocalUses;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: gdevanla
 * Date: 6/23/13
 * Time: 5:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class Core extends BodyTransformer {

    public final String clazzNameofMUT;
    public final String mutSignature;

    public final LinkedList<Oracle> oraclesFound = new LinkedList<Oracle>();

    public Core(String clazzNameOfMUT, String mutSignature){
        this.clazzNameofMUT = clazzNameOfMUT;
        this.mutSignature = mutSignature;
        //this is a static instance and therefore has to be here
       // PackManager.v().getPack("jtp").add(new Transform("jtp."+ Math.random(), this));

    }


    public void  runAnalysis(int outputFormat, boolean verbose){
        soot.G.reset();
        Options.v().set_verbose(verbose);
        Options.v().set_output_format(outputFormat);
        String[] sootArguments = new String[]{"-process-dir",
                OracleFinderConfiguration.v().getAppTestsSourceFolder(),
                "-cp", OracleFinderConfiguration.v().getSootClassPath()};
        PackManager.v().getPack("jtp").add(new Transform("jtp.myTransformer", this));

        soot.Main.main(sootArguments);
    }

    public void  runAnalysis(int outputFormat, boolean verbose, String inputFileName){
        soot.G.reset();
        //Options.v().set_verbose(verbose);
        Options.v().set_output_format(outputFormat);
        String[] sootArguments = new String[]{inputFileName,
                "-cp", OracleFinderConfiguration.v().getSootClassPath()};
        PackManager.v().getPack("jtp").add(new Transform("jtp.myTransformer", this));

        soot.Main.main(sootArguments);

    }

    @Override
    protected void internalTransform(Body body, String s, Map map) {
        if (body.getMethod().isConstructor()){
            System.out.println("*****" + body.getMethod());
            return;
        }


        if (!isTestMethod(body)) return;
        System.out.println("Before checking for MUT");
        if (!containsMUT(body)) return;
        System.out.println("Ready for method, since it has MUT=" + body.getMethod());

        //oraclesFound.addAll(new SimpleReturnPatternFinder(body, this.mutSignature).getAllOccurences());
        try {
            oraclesFound.addAll(new AssertOnReturnObjectsMethod(body, this.mutSignature).getAllOccurences());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        //System.out.println("Body Name= " + body.getMethod().getName());
        //for (Unit unit:body.getUnits()){
        //    System.out.println((Stmt)unit + "," + unit.getUseBoxes());
        //}
    }

    private boolean containsMUT(Body body){
        for(Unit unit:body.getUnits()){
            if (((Stmt)unit).containsInvokeExpr()){
                InvokeExpr expr = (InvokeExpr)((Stmt) unit).getInvokeExpr();
                System.out.println(expr.getMethod().getSignature());
                System.out.println(expr.getMethod().getSubSignature());
                if (expr.getMethod().getSignature().contains(mutSignature)){
                    return true;
                }
            }
        }
        return false;
    }

    //looks for junit 3.8.2 test methods
    private boolean isTestMethod(Body body){
        SootClass testerClass = body.getMethod().getDeclaringClass();
        //System.out.println(testerClass.getName() + ":" + testerClass.getSuperclass().getName());
        if ( testerClass.getSuperclass().getName().equals("junit.framework.TestCase") ){
            if (body.getMethod().getName().startsWith("test")){
                //System.out.println("Returning true for " + body.getMethod().getName() +":" + body.getMethod().getSignature());
                return true;
            }
        }

        return false;
    }


    public static void main(String[] args){

       /* String appBaseFolder = "/Users/gdevanla/Dropbox/private/se_research/myprojects/assist/apps/StateCarver/assist";
        String inputSourceFolder =  "/Users/gdevanla/Dropbox/private/se_research/myprojects/assist/apps/StateCarver/TestArtifacts/src/test/java";
        String sootClassPath = appBaseFolder + "/" + "target/classes" + ":" + inputSourceFolder;

        sootClassPath = sootClassPath  + ":/System/Library/Frameworks/JavaVM.framework/Classes/classes.jar"
                + ":" + "/System/Library/Frameworks/JavaVM.framework/Classes/jce.jar"
                + ":" + "/Users/gdevanla/.m2/repository/com/thoughtworks/xstream/xstream/1.4.4/xstream-1.4.4.jar"
                + ":" + "/Users/gdevanla/.m2/repository/junit/junit/3.8.1/junit-3.8.1.jar";
         */

        Core c = new Core("Apples", "com.ser.oraclefinder.testartifacts.Apples.add(int)");
        c.runAnalysis(Options.output_format_J, false);
        System.out.println(c.oraclesFound.size());

    }

}
