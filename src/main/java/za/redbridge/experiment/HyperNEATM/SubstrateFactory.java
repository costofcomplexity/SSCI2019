package za.redbridge.experiment.HyperNEATM;

import org.encog.neural.hyperneat.substrate.Substrate;
import org.encog.neural.hyperneat.substrate.SubstrateNode;
import za.redbridge.experiment.NEATM.sensor.SensorMorphology;
import za.redbridge.simulator.sensor.AgentSensor;

/**
 * Substrate for Khepera
 *
 * Created by Danielle and Alexander on 2018/06/20.
 */
public class SubstrateFactory
{

    /* ---------------------------------------------------------------------------
       Creates a HyperNEAT substrate which geometrically resembles the morphology
       (placement of sensors and actuators) of a Khepera robot.

       Each node in the substrate has a Cartesian coordinate.

       Centre-point of the robot/substrate is (0,0).
     -----------------------------------------------------------------------------*/

    public static Substrate createKheperaSubstrate(double distBetweenEachSensor, double radius)

    {
        Substrate substrate = new Substrate(2); // Substrate exists on a single 2D-Cartesian coordinate plane.
        for (double theta = 0; theta < Math.PI*2; theta+=distBetweenEachSensor)     // loop around circle substrate and place a possible
        {                                                                           // input sensor node, each separated by distBetweenEachSensor
           SubstrateNode inputNode = substrate.createInputNode();
           inputNode.getLocation()[0] = radius*Math.cos(theta);     // x = radius*Math.cos(theta)
           inputNode.getLocation()[1] = radius*Math.sin(theta);     // y = radius*Math.cos(theta)
        }
        double outputYcoord = 0 - radius - radius*0.25;
        double outputXcoord = radius*0.3333;

        SubstrateNode outputNodeLeft = substrate.createOutputNode();
        outputNodeLeft.getLocation()[0] = outputXcoord;                 // location of left movement actuator output node on substrate
        outputNodeLeft.getLocation()[1] = outputYcoord;

        SubstrateNode outputNodeRight = substrate.createOutputNode();
        outputNodeRight.getLocation()[0] = -outputXcoord;               // location of right movement actuator output node on substrate
        outputNodeRight.getLocation()[1] = outputYcoord;
        for (SubstrateNode inputNode: substrate.getInputNodes()) {
            for (SubstrateNode outputNode : substrate.getOutputNodes())             // Link every input node to every output node.
            {                                                                       // A CPPN can decide which of these connections will exist in the ANN
                substrate.createLink(inputNode, outputNode);                        // that the substrate decodes to, based on connection weight it assigns.
            }
        }
        return substrate;
    }

    public static Substrate createKheperaIIISubstrate(double number_of_sensors, double radius)

    {
        Substrate substrate = new Substrate(2); // Substrate exists on a single 2D-Cartesian coordinate plane.

        double distBetweenEachSensor = (Math.PI*2)/number_of_sensors;

        for (double theta = 0; theta < Math.PI*2; theta+=distBetweenEachSensor)     // loop around circle substrate and place a possible
        {                                                                           // input sensor node, each separated by distBetweenEachSensor
            SubstrateNode inputNode = substrate.createInputNode();
            inputNode.getLocation()[0] = radius*Math.cos(theta);     // x = radius*Math.cos(theta)
            inputNode.getLocation()[1] = radius*Math.sin(theta);     // y = radius*Math.cos(theta)
        }
        double outputYcoord = 0 - radius - radius*0.25;
        double outputXcoord = radius*0.3333;

        SubstrateNode outputNodeLeft = substrate.createOutputNode();
        outputNodeLeft.getLocation()[0] = outputXcoord;                 // location of left movement actuator output node on substrate
        outputNodeLeft.getLocation()[1] = outputYcoord;

        SubstrateNode outputNodeRight = substrate.createOutputNode();
        outputNodeRight.getLocation()[0] = -outputXcoord;               // location of right movement actuator output node on substrate
        outputNodeRight.getLocation()[1] = outputYcoord;
        for (SubstrateNode inputNode: substrate.getInputNodes()) {
            for (SubstrateNode outputNode : substrate.getOutputNodes())             // Link every input node to every output node.
            {                                                                       // A CPPN can decide which of these connections will exist in the ANN
                substrate.createLink(inputNode, outputNode);                        // that the substrate decodes to, based on connection weight it assigns.
            }
        }
        return substrate;
    }


    public static Substrate createSubstrateFromSensorMorphology(SensorMorphology sensorMorphology, double radius)
    {
        Substrate substrate = new Substrate(2); // Substrate exists on a single 2D-Cartesian coordinate plane.

        for (int i =0;i<sensorMorphology.getNumSensors();i++)     // loop around circle substrate and place a possible
        {                                                                           // input sensor node, each separated by distBetweenEachSensor
            SubstrateNode inputNode = substrate.createInputNode();
            AgentSensor sensor = sensorMorphology.getSensor(i);
            float position = sensor.getBearing();
            if(position<0){
                position = position + (float) Math.PI*2;
            }
            inputNode.getLocation()[0] = radius*Math.cos(position);     // x = radius*Math.cos(theta)
            inputNode.getLocation()[1] = radius*Math.sin(position);     // y = radius*Math.cos(theta)
        }
        double outputYcoord = 0 - radius - radius*0.25;
        double outputXcoord = radius*0.3333;

        SubstrateNode outputNodeLeft = substrate.createOutputNode();
        outputNodeLeft.getLocation()[0] = outputXcoord;                 // location of left movement actuator output node on substrate
        outputNodeLeft.getLocation()[1] = outputYcoord;

        SubstrateNode outputNodeRight = substrate.createOutputNode();
        outputNodeRight.getLocation()[0] = -outputXcoord;               // location of right movement actuator output node on substrate
        outputNodeRight.getLocation()[1] = outputYcoord;
        for (SubstrateNode inputNode: substrate.getInputNodes()) {
            for (SubstrateNode outputNode : substrate.getOutputNodes())             // Link every input node to every output node.
            {                                                                       // A CPPN can decide which of these connections will exist in the ANN
                substrate.createLink(inputNode, outputNode);                        // that the substrate decodes to, based on connection weight it assigns.
            }
        }
        return substrate;
    }

}