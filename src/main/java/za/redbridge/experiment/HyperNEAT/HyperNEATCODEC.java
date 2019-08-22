package za.redbridge.experiment.HyperNEAT;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.ea.codec.GeneticCODEC;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.genetic.GeneticError;
import org.encog.neural.hyperneat.substrate.Substrate;
import org.encog.neural.hyperneat.substrate.SubstrateLink;
import org.encog.neural.hyperneat.substrate.SubstrateNode;
import org.encog.neural.neat.NEATCODEC;
import org.encog.neural.neat.NEATLink;
import org.encog.neural.neat.NEATNetwork;
import za.redbridge.experiment.NEAT.NEATPopulation;
import za.redbridge.experiment.NEATM.ActivationSteepenedShiftedSigmoid;
import za.redbridge.experiment.NEATM.NEATMNetwork;
import za.redbridge.experiment.NEATM.sensor.SensorMorphology;


import java.io.Serializable;
import java.util.*;

/**
 * CODEC FOR HYPERNEAT
 * Makes a phenome with a fixed SensorMorpholoy
 *
 * Created by Danielle and Alexander on 2019/02/07.
 */
public class HyperNEATCODEC implements GeneticCODEC, Serializable
{
    private double minWeight = 0.2;
    private double maxWeight = 5.0;
    SensorMorphology sensorMorphology;

    public HyperNEATCODEC(SensorMorphology sensorMorphology){
        this.sensorMorphology = sensorMorphology;
    }

    /**
     * {@inheritDoc}
     */



    @Override
    public MLMethod decode(final Genome genome) {
        final NEATPopulation pop = (NEATPopulation) genome.getPopulation();
        final Substrate substrate = pop.getSubstrate();
        return decode(pop, substrate, genome);
    }

    public MLMethod decode(final NEATPopulation pop, final Substrate substrate, final Genome genome) {
        //obtain the CPPN

        final NEATCODEC neatCodec = new NEATCODEC();
        final NEATNetwork cppn = (NEATNetwork) neatCodec.decode(genome);


        //setup for creating a new ANN from CPPN and substrate
        final List<NEATLink> linkList = new ArrayList<NEATLink>();

        final ActivationFunction[] afs = new ActivationFunction[substrate.getNodeCount()];
        //Activation Function for the right output
        final ActivationFunction af = new ActivationSteepenedShiftedSigmoid();
        // all activation functions are the same
        for (int i = 0; i < afs.length; i++) {
            afs[i] = af;
        }

        final double c = this.maxWeight / (1.0 - this.minWeight);
        final MLData input = new BasicMLData(cppn.getInputCount());
        // @TODO DONE


        // First create all of the non-bias links and create a list sensor morphology

        for (final SubstrateLink link : substrate.getLinks()) {
            final SubstrateNode source = link.getSource();
            final SubstrateNode target = link.getTarget();

            int index = 0;
            for (final double d : source.getLocation()) {
                input.setData(index++, d);
            }
            for (final double d : target.getLocation()) {
                input.setData(index++, d);
            }
            final MLData output = cppn.compute(input);

            double weight = output.getData(0);

            if (Math.abs(weight) > this.minWeight) {
                weight = (Math.abs(weight) - this.minWeight) * c * Math.signum(weight);
                linkList.add(new NEATLink(source.getId(), target.getId(), weight));
            }
        }

        // now create biased links

        input.clear();
        final int d = substrate.getDimensions();
        final List<SubstrateNode> biasedNodes = substrate.getBiasedNodes();
        for (final SubstrateNode target : biasedNodes) {
            for (int i = 0; i < d; i++) {
                input.setData(d + i, target.getLocation()[i]);
            }

            final MLData output = cppn.compute(input);

            double biasWeight = output.getData(1);
            if (Math.abs(biasWeight) > this.minWeight) {
                biasWeight = (Math.abs(biasWeight) - this.minWeight) * c
                        * Math.signum(biasWeight);
                linkList.add(new NEATLink(0, target.getId(), biasWeight));
            }
        }

        // check for invalid neural network
        if (linkList.size() == 0) {
            return null;
        }

        Collections.sort(linkList);



        if(sensorMorphology==null) System.out.println("hello null");
        final NEATMNetwork network = new NEATMNetwork(sensorMorphology.getNumSensors(), substrate.getOutputCount(), linkList, afs, sensorMorphology);
        network.setActivationCycles(substrate.getActivationCycles());

        return network;

    }
    @Override
    public Genome encode(final MLMethod phenotype) {
        throw new GeneticError(
                "Encoding of a HyperNEAT network is not supported.");
    }
}
