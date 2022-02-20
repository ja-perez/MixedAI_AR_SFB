
package com.arcore.MixedAIAR;

import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import android.app.Activity;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import java.lang.Math;
import java.io.InputStream;

public class DecisionAlgorithm implements Runnable {


    private final MainActivity mInstance;
    //boolean recent_close;
    private int objind;
    int dindex;
    //List<Float> prevquality ;
    //private float cacheArray[] = new float[5000];
    private int window;
    //private int dindex;
   // private HashMap<Integer, ArrayList<Float> > predicted_dis;



    public DecisionAlgorithm(MainActivity mInstance, int objindex, int ww, int dindex) {










        this.mInstance=mInstance;
//        this.recent_close=close;
//        prevquality= new ArrayList<>(prevq);
//        this.cacheArray= cacheArr;
        this.objind=objindex;
        window=ww;
        this.dindex=dindex;
//        d1=dis;
//        predicted_dis =new HashMap<Integer, ArrayList<Float>>(predicted_d);
    }

    @Override
    public void run() {



        long startTime = System.currentTimeMillis();


            int ind= objind;
            int finalInd = ind;
           float d1 = mInstance.predicted_distances.get(finalInd).get(0);// gets the first time, next 1s of every object, ie. d1 of every obj




// these lines need to be defined in a function to call then from decision algorithm to get result from there
            if (d1 <= mInstance.d1_prev.get(finalInd) && mInstance.closer.get(finalInd) == false)
                mInstance.closer.set(finalInd, true);

            else if (d1> mInstance.d1_prev.get(finalInd) && mInstance.closer.get(finalInd) == true) {
                mInstance.closer.set(finalInd, false);
                mInstance.obj_backward.set(ind, false);
                d1= mInstance.renderArray[finalInd].return_distance( );

            }

            mInstance.d1_prev.set(ind, d1);
            boolean close = mInstance.closer.get(finalInd);
            float prev_cache= mInstance.cacheArray[finalInd]; // store prev cache before changing in prediction code
            //int finalInd1 = ind;

            final float[] finalD = {d1};
           // current_thread.set(ind , new Thread(){
              //  @Override
              //  public void run(){

            List<String> temppredict3 = new ArrayList<String>();

            temppredict3=mInstance.predictwindow(mInstance,mInstance.closer,mInstance.cacheArray[finalInd], mInstance.prevquality.get(finalInd), finalD[0], window, finalInd, dindex + mInstance.decision_p-1, mInstance.predicted_distances.get(finalInd));

            mInstance.closer.set(finalInd, close);


             mInstance.cacheArray[finalInd]= prev_cache;

            float eb1 = Float.parseFloat(temppredict3.get(0));
            String predictlog = temppredict3.get(1);
            String logbestpredicteb = temppredict3.get(2);

            float cur_eb1= mInstance.best_cur_eb.get(finalInd);

            String[] cur_q = predictlog.split(",");
            float cur_q1 = Float.parseFloat(cur_q[(cur_q.length) - 1]);
            float prevq1 =mInstance. prevquality.get(finalInd);

                    //bckward manner for eb2
            mInstance.updatecloser(mInstance.predicted_distances.get(finalInd).get(0), mInstance.predicted_distances.get(finalInd).get(2), finalInd);

            if (mInstance.closer.get(finalInd) && mInstance.obj_backward.get(finalInd)==false){

                        ArrayList<ArrayList<Float>> temppredict4 = new  ArrayList<ArrayList<Float>>();

                        temppredict4=mInstance.findW(finalInd);
                        // int newW= Math.round( temppredict4.get(0));
                        float ux= temppredict4.get(0).get(0);
                        float uz= temppredict4.get(1).get(0);
                        ArrayList<Float> newdistance= new ArrayList<>( temppredict4.get(2));
                        Collections.reverse(newdistance); // need to reverse distanecs from closer to farther to the objcet
                        int newW=( newdistance.size()-mInstance.decision_p- 1 - (mInstance.decision_p-1) )/ mInstance.decision_p; // here newdistance size id equal to maxtime/2

                        if(newW>1) {
                            // ASSUME IT'S GETTing farther from upos_x + (w-1)*dis_interval to actual upos

                            //can't go farther than newW and also more than w, in the other words


                            mInstance.closer.set(finalInd, false);
                            //float newdis  = newdistance.get(0);
                            float d11 = newdistance.get(mInstance.decision_p-1); // the first element reversed

                            List<String> temppredict5 = new ArrayList<String>();

                            float prev_cache2= mInstance.cacheArray[finalInd];
                            temppredict5=mInstance.predictwindow(mInstance, mInstance.closer,
                                    mInstance.cacheArray[finalInd], 1, d11, newW, finalInd, (2*mInstance.decision_p)-1 , newdistance);// for this case since newdis array has current dis as an extra, we start from 2p-1, refer to notes page 339

                            //just chnged to newW-1 in prediction above
                            mInstance.cacheArray[finalInd]= prev_cache2;

                            float eb2 = Float.parseFloat(temppredict5.get(0));
                            String predictlog2 = temppredict3.get(1);
                            String logbestpredicteb2 = temppredict3.get(2);

                            String[]  cur_ebb =    logbestpredicteb2.split(",");
                            float cur_eb2 =Float.parseFloat(cur_ebb[0]);
                            String[] cur_qq = predictlog2.split(",");
                            float cur_q2 = Float.parseFloat(cur_qq[0]);
                            float  prevq2 = cur_q2;

                            float ebb1=0,ebb2 = 0;
                            if (newW < window) {
                                ebb2 =(eb2);
                                for (int i=0; i<newW ;i++)
                                    ebb1 +=  Float.parseFloat(cur_ebb[newW - i]);
                            }

                            else{
                                ebb1 =(eb1);
                                for (int i = 0; i< window - 1; i++)
                                    ebb2 +=Float.parseFloat(cur_ebb[i]);
                                if ( Float.parseFloat(cur_ebb[window -1]) <= 0)
                                    ebb2 += Float.parseFloat(cur_ebb[window -1]);
                                else{
                                    int j = window- 1;
                                    while (j < (cur_ebb.length - 1) && Float.parseFloat(cur_ebb[ j])>=0 )
                                        j = j + 1;
                                    if (j < cur_ebb.length - 1)
                                        ebb2 += Float.parseFloat(cur_ebb[ j]);
                                }
                            }


                            //last item in logbestpredicted2  cur_q2 = first item in predictlog2


                            if (ebb1<ebb2)
                            {   cur_eb1 = 0;
                                // #means that we have selected from backward in posituve way that should be neg
                                int j = 0;
                                while (j < (cur_qq.length - 1) &&  Float.parseFloat(cur_ebb[ j]) >= 0 )
                                    // #looking for true negative value of eb for decimated obj
                                    j = j + 1;

                                if (j < cur_qq.length - 1)
                                    cur_eb1 =Float.parseFloat(cur_ebb[j]);


                                prevq1 =(prevq2);
                                cur_q1 =(cur_q2);
                                finalD[0] =d11;


                            }

                            //#else:let it be as it is
                            // #eb2 is useless since it is for the farthest window, so we need chain of best eb 's
                            mInstance.closer.set(finalInd, true);
                            mInstance.obj_backward.set(finalInd, true);
                        }
                        // #end of if new>1
                        //#end of if closer and bckward
                    }


            float distance= mInstance.renderArray[finalInd].return_distance();
            String last_dis=  mInstance.distance_log.get(finalInd);
             mInstance.distance_log.set(finalInd, last_dis +","+ distance);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            String last_time= mInstance.time_log.get(finalInd);
             mInstance.time_log.set(finalInd, last_time+ "," +dateFormat.format(new Date()).toString() );


                    //'''upfdate everythong finally'''
            String lastq_log = mInstance.quality_log.get(finalInd);
            mInstance.quality_log.set(finalInd, lastq_log + cur_q1 + ",");

            float cur_blc = mInstance.eng_blc.get(finalInd);
            mInstance.eng_blc.set(finalInd, cur_eb1 + cur_blc);
            mInstance.prevquality.set(finalInd,prevq1);
            int indq = mInstance.excelname.indexOf(mInstance.renderArray[finalInd].fileName);// search in excel file to find the name of current object and get access to the index of current object
                    // excel file has all information for the degredation model
            float gamma = mInstance.excel_gamma.get(indq);
            float a = mInstance.excel_alpha.get(indq);
            float b = mInstance.excel_betta.get(indq);
            float c = mInstance.excel_c.get(indq);
            float deg_error =
 (float)(Math.round((float)(mInstance.Calculate_deg_er(a, b, c, finalD[0], gamma, cur_q1) * 10000))) / 10000;

            String lasterror= mInstance.deg_error_log.get(finalInd);

             mInstance.deg_error_log.set(finalInd, lasterror+ Float.toString(deg_error) + ",");
            String lastgpu= mInstance.GPU_Ut_log.get(finalInd);

            float current_tris= mInstance.total_tris - ( ( 1-cur_q1) *mInstance. excel_tris.get(indq));
            float GPU_usage = mInstance.compute_GPU_eng( mInstance.decision_p / mInstance.decision_p,current_tris );// for every sec
            mInstance.GPU_Ut_log.set(finalInd,  lastgpu+ Float.toString(GPU_usage) + ",");

           float filesize= mInstance.excel_filesize.get(indq);

        String last_decEng= mInstance.eng_dec.get(finalInd);
        float netw_eng=update_e_dec_req( filesize, cur_q1,finalInd );
        mInstance.updatednetw[finalInd]= netw_eng;
        mInstance.eng_dec.set(finalInd, last_decEng+ Float.toString(netw_eng ) + ",");



             mInstance.updateratio[finalInd] = cur_q1;

            if (cur_q1 != 1) {
                        // cacheArray[finalInd] = cur_q1;
                mInstance.fthr.set(finalInd, cur_q1);
            } //else


/*
        long endTime = System.currentTimeMillis();



        String currentFolder2 = mInstance.getExternalFilesDir(null).getAbsolutePath();
        String FILEPATH2 = currentFolder2 + File.separator + "alg_time.txt";

        PrintWriter fileOut2 = null;
        PrintStream streamOut2 = null;

        try {
            fileOut2 = new PrintWriter(new FileOutputStream(FILEPATH2, true));

            ///fileOut2.println();

            //fileOut2.println("object information");
            //int ind=0;

            fileOut2.println( dateFormat.format(new Date()).toString()+"  alg_time_milliseconds " + (endTime - startTime) );

            fileOut2.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

*/


//this code was in tracking update body before, I moved it here
//        int i=finalInd;
//        float ratio = mInstance.updateratio[i];
//
//        /// if ((ratio ) != ratioArray[i] && (ratio==0.2f||  ratio==0.3f  ) ) {
//        if ((ratio ) != mInstance.ratioArray[i]   ) {
//            mInstance.total_tris= mInstance.total_tris- (mInstance.ratioArray[i]* mInstance.o_tris.get(i));// total =total -1*objtris
//            mInstance.ratioArray[i] = ratio;
//            mInstance.total_tris = mInstance.total_tris+ (mInstance.ratioArray[i]* mInstance.o_tris.get(i));// total = total + 0.8*objtris
//
//            mInstance.renderArray[i].decimatedModelRequest(ratio , i);
//
//
//            if (ratio  != 1 && ratio !=mInstance.cacheArray[i] ) {
//                mInstance.cacheArray[i] = (ratio);
//                mInstance.Server_reg_Freq.set(i, mInstance.Server_reg_Freq.get(i)+1);
//            }
//        }

               // }
           /// });







    }

    public float  update_e_dec_req(  float size, float qual, int ind){
        //   '''this is to update energy consumption for decimation '''
        //return 0;

        if (qual==1 || qual== mInstance.cacheArray[ind])
            return 0;

        for (int i=0;i<mInstance.objectCount; i++)
            if(   i!= ind && mInstance.renderArray[ind].fileName  ==  mInstance.renderArray[i].fileName &&  mInstance.cacheArray[i] == qual) {
              return  0;
            }
        //assume net is 5g
        float eng_network=  (size/ (1000000f * mInstance.bwidth )) * 1.5f * 1000f;// in milli joule


         return eng_network;}//in mili joule

    }
