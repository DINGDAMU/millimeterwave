package org.onosproject.mmcli;

import org.apache.commons.math3.special.Erf;

public final  class Psuccess {

    public  double getPs(double d){
        double pl=0.0;
        double alpha=0.0;  //dB
        double beta=0.0;
        double sigma=0.0;   //dB
        double sigma_lin=0.0;
        double xsi_lin=0.0;
        double xsi=0.0;
        double fc;
        String pl_model = "28";
        if(pl_model == "28"){
            alpha=72.0;
            beta=2.92;
            sigma=8.7;
            sigma_lin=Math.pow(10,sigma/10);
            xsi_lin=Math.pow(sigma_lin,2);
            xsi=10*Math.log10(xsi_lin);
            pl=alpha+10*beta*Math.log10(d);
        }else if (pl_model == "73"){
            alpha =86.6;
            beta=2.45;
            sigma =8.0;
            sigma_lin=Math.pow(10,sigma/10);
            xsi_lin=Math.pow(sigma_lin,2);
            xsi=10*Math.log10(xsi_lin);
            pl=alpha+10*beta*Math.log10(d);

        }else if(pl_model == "3gpp"){
            fc=2.5;
            pl=22.7+36.7*Math.log10(d)+26*Math.log10(fc);
            beta=3.67;
            alpha=70.0;
        }else{
            System.out.println("Please type the right pl_model");
        }

        double lamda = 100.0; //density of mm-wave links
        double B = 2e9;         //mm-wave bandwidth
        double C = 0.11;      //fractional Los area in the model developed


        double Gmax = 18.0;  //dB
        double Gmax_lin = Math.pow(10,Gmax/10);

        double Pb = 30.0;   //dB
        double Pb_lin = Math.pow(10,Pb/10);

        double pn= -174.0 +10*Math.log10(B)+10;
        double pn_lin = Math.pow(10,pn/10);

        double pl_lin = Math.pow(10,pl/10);

        double SNR0 = Pb + Gmax - pn;
        double SNR0_lin = Math.pow(10,SNR0/10);

        double SNR = SNR0 - pl;
        double SNR_lin = Math.pow(10,SNR/10);

        //xsi corresponding path-loss standard deviation
        double xsi_l_lin = 5.2;
        double xsi_n_lin = 7.6;

        //beta_l_n = one meter loss #dB
        double beta_l_n = alpha; //db
        double beta_l_n_lin = Math.pow(10,beta_l_n/10);

        //ml_db = -0.1 *beta_l_lin *log(10)
        double ml=-Math.log(beta_l_n_lin);
        double sigma_l = 0.1 * xsi_l_lin *Math.log(10);

        //mn_db = -0.1*beta_n_lin * log(10)
        double mn = -Math.log(beta_l_n_lin);
        double sigma_n = 0.1 * xsi_n_lin *Math.log(10);

        double tau = 3.0;
        double tau_lin = Math.pow(10, tau/10);


        //((Pb_lin*Gmax_lin)/(pl_lin*pn_lin)
        double factor = SNR_lin/tau_lin;

        double pc1_q1 = qfun( (Math.log((Math.pow(d,beta)/factor))-ml)/sigma_l);
        double pc1_q2 = qfun( (Math.log((Math.pow(d,pl)/factor))-mn)/sigma_n);
        double pc1 = Math.pow(d,2)*(pc1_q1-pc1_q2);
        double pc2 = pc_c1(sigma_l,ml,d,beta,factor)*pc_c2(sigma_l,ml,d,beta,factor);
        double pc3 = pc_c1(sigma_n,mn,d,beta,factor)*(1/C - pc_c2(sigma_n,mn,d,beta,factor));
        double pc = pc1 + pc2 + pc3 ;
        double Lamda_a = lamda * Math.PI * C * pc;
        double Ma = Lamda_a /lamda;
        double ps = 1 - Math.exp(-lamda*Ma*factor);
        return ps;
    }

    public static double qfun(double x){
        return 0.5*Erf.erfc(x/Math.sqrt(2));
    }

    public static double pc_c1(double sigma, double m ,double d,double beta,double factor){
        return Math.pow(factor,2/beta)*Math.exp(2*((sigma*sigma)/(beta*beta))+2*(m/beta));
    }

    public static  double pc_c2(double sigma,double m,double d,double beta,double factor){
        return qfun((sigma*sigma*(2/beta)-Math.log(Math.pow(d,beta/factor))+m)/sigma);

    }






}
