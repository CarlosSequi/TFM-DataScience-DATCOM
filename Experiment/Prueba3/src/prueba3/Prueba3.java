/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prueba3;

// Para usar el clasificador HoeffdingTree directamente sin hacer
// uso de la clase HT(que es lo mismo pero modificada para el uso de
// restricciones monotónicas)
//import moa.classifiers.trees.HoeffdingTree;
import com.yahoo.labs.samoa.instances.Instance;
import moa.classifiers.Classifier;
import moa.core.TimingUtils;
import moa.streams.generators.RandomRBFGenerator;
//import weka.core.Instance;
//import weka.core.Instances;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import weka.core.converters.ArffLoader.ArffReader;
import moa.streams.ArffFileStream;

public class Prueba3 {

        public Prueba3(){
        }

        public void run(/*int numInstances, boolean isTesting*/) throws FileNotFoundException, IOException{
            
                // declaramos el clasificador que queremos utilizar
                Classifier learner = new HT();
                
                // definimos el data set a leer
                String nombreDataSet = "D:\\TFM-DataScience-DATCOM\\Experiment\\Data sets\\era\\era.arff";
                
                // inicializamos la cantidad de instancias a leer
                int numInstances = 1000;
                
                // definimos el número de instancias por defecto
               /* 
                BufferedReader reader = new BufferedReader(new FileReader(nombreDataSet));
                ArffReader arff = new ArffReader(reader, 1000);
                Instances data = arff.getStructure();
                data.setClassIndex(data.numAttributes()-1);
                Instance inst;
                while((inst=arff.readInstance(data)) != null){ data.add(inst);}
                numInstances = data.numInstances();*/
                //System.out.print(data);
                
                // declaramos el flujo de datos y lo dejamos listo para su uso
                /*RandomRBFGenerator stream = new RandomRBFGenerator();
                stream.prepareForUse();*/
                ArffFileStream stream = new ArffFileStream(nombreDataSet,3);
                stream.prepareForUse();

                // establecemos la cabecera de los datos de streaming en el
                // contexto del clasificador, para dejarlo listo para su uso
                learner.setModelContext(stream.getHeader());
                learner.prepareForUse();

                int numberSamplesCorrect = 0; // cantidad de aciertos
                int numberSamples = 0; // cantidad de muestras
                
                // para la toma de tiempo de cómputo
                boolean preciseCPUTiming = TimingUtils.enablePreciseTiming();
                long evaluateStartTime = TimingUtils.getNanoCPUTimeOfCurrentThread();
                
                // leemos datos del flujo mientras haya y aplicamos
                // test-then-train sobre cada dato
                while (stream.hasMoreInstances() && numberSamples < numInstances) {
                        // tomamos el siguiente dato
                        Instance trainInst = stream.nextInstance().getData();
                        //Instance trainInst = (Instance) data.get(numberSamples);
                        //System.out.print(trainInst+"\n");
                        // FASE TEST
                        //if (isTesting) {
                                // si lo clasifica de forma correcta sumamos
                                // un acierto a las estadisticas
                                if (learner.correctlyClassifies(trainInst)){
                                        numberSamplesCorrect++;
                                }
                        //}
                        numberSamples++;
                        
                        // FASE TRAIN
                        learner.trainOnInstance((com.yahoo.labs.samoa.instances.Instance)trainInst);
                }
                double accuracy = 100.0 * (double) numberSamplesCorrect/ (double) numberSamples;
                double time = TimingUtils.nanoTimeToSeconds(TimingUtils.getNanoCPUTimeOfCurrentThread()- evaluateStartTime);
                System.out.println(numberSamples + " instances processed with " + accuracy + "% accuracy in "+time+" seconds.");
        }

        public static void main(String[] args) throws IOException {
                Prueba3 exp = new Prueba3();
                exp.run(/*1000, true*/);
        }
}