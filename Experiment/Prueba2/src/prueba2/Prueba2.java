/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prueba2;

// Para usar el clasificador HoeffdingTree directamente sin hacer
// uso de la clase HT(que es lo mismo pero modificada para el uso de
// restricciones monotónicas)
//import moa.classifiers.trees.HoeffdingTree;
import moa.classifiers.Classifier;
import moa.core.TimingUtils;
import moa.streams.generators.RandomRBFGenerator;
import com.yahoo.labs.samoa.instances.Instance;
import java.io.IOException;


public class Prueba2 {

        public Prueba2(){
        }

        public void run(int numInstances, boolean isTesting){
            
                // declaramos el clasificador que queremos utilizar
                Classifier learner = new HT();
                
                // declaramos el flujo de datos y lo dejamos listo para su uso
                RandomRBFGenerator stream = new RandomRBFGenerator();
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
                        
                        // FASE TEST
                        if (isTesting) {
                                // si lo clasifica de forma correcta sumamos
                                // un acierto a las estadisticas
                                if (learner.correctlyClassifies(trainInst)){
                                        numberSamplesCorrect++;
                                }
                        }
                        numberSamples++;
                        
                        // FASE TRAIN
                        learner.trainOnInstance(trainInst);
                }
                double accuracy = 100.0 * (double) numberSamplesCorrect/ (double) numberSamples;
                double time = TimingUtils.nanoTimeToSeconds(TimingUtils.getNanoCPUTimeOfCurrentThread()- evaluateStartTime);
                System.out.println(numberSamples + " instances processed with " + accuracy + "% accuracy in "+time+" seconds.");
        }

        public static void main(String[] args) throws IOException {
                Prueba2 exp = new Prueba2();
                exp.run(1000, true);
        }
}