#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sat Oct  6 09:12:01 2018

@author: dduque
"""


import os
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.backends.backend_pdf import PdfPages
from matplotlib.ticker import AutoMinorLocator
os.chdir('/Users/dduque/MacWorkspace/S-Pulse/RobustCSP/results')
filepath = './RN_results_labels.csv'
data = pd.read_csv(filepath)
test = data[(data.scenarios==1000) & (data.pulse_time<=10)]


mat_out = []

params_def = ['w%i,b%i' %(w,b) for w in [1,2,3]  for b in [1,2] ]
params_def.insert(0,'')
params_def = np.array(params_def)
bar_width = 0.1
cols = ['red', 'blue', 'green', 'purple', 'yellow']
num_sce = 1000
rep = 10 #num replicas per experiment
variabilities = []
for i in range(1,31):
    RIi = data[(data.instance=='RI%i' %(i)) & (data.scenarios==num_sce)]
    Ws = np.unique(RIi.W)
    bs = np.unique(RIi.B)
    lfs = np.unique(RIi.labels_frac)
    lfs = lfs[lfs<=1.5]
    if RIi.pulse_time.mean()>0 and i>0:
        print('Doing plot for %i' %(i))
        fig1, ax1 = plt.subplots(figsize=(3, 4))
        fig2, ax2 = plt.subplots(figsize=(3, 4))
        fig3, ax3 = plt.subplots(figsize=(3, 4))
        for (j,lf) in enumerate(lfs):
            lf_data = RIi[RIi.labels_frac==lf]
#            averages = np.array([lf_data[lf_data.B==b].mean()['pulse_time'] for b in bs])
#            max_vals = np.array([lf_data[lf_data.B==b].max()['pulse_time'] for b in bs])
#            min_vals = np.array([lf_data[lf_data.B==b].min()['pulse_time'] for b in bs])
#            SDs = np.array([lf_data[lf_data.B==b].std()['pulse_time'] for b in bs])
#            averages_t = np.array([lf_data[lf_data.B==b].mean()['total_time'] for b in bs])
#            max_vals_t = np.array([lf_data[lf_data.B==b].max()['total_time'] for b in bs])
#            min_vals_t = np.array([lf_data[lf_data.B==b].min()['total_time'] for b in bs])
#            SDs_t = np.array([lf_data[lf_data.B==b].std()['total_time'] for b in bs])
           
            
            averages = np.array([lf_data[b*rep:(b+1)*rep].mean()['pulse_time'] for b in range(6)])
            max_vals = np.array([lf_data[b*rep:(b+1)*rep].max()['pulse_time'] for b in range(6)])
            min_vals = np.array([lf_data[b*rep:(b+1)*rep].min()['pulse_time'] for b in range(6)])
            SDs = np.array([lf_data[b*rep:(b+1)*rep].std()['pulse_time'] for b in range(6)])
            for b in range(6):
                assert lf_data[b*rep:(b+1)*rep].std()['B'] ==0 
            averages_t = np.array([lf_data[b*rep:(b+1)*rep].mean()['total_time'] for b in range(6)])
            max_vals_t = np.array([lf_data[b*rep:(b+1)*rep].max()['total_time'] for b in range(6)])
            min_vals_t = np.array([lf_data[b*rep:(b+1)*rep].min()['total_time'] for b in range(6)])
            SDs_t = np.array([lf_data[b*rep:(b+1)*rep].std()['total_time'] for b in range(6)])
            
            
            mat_out.append(np.hstack((i,lf*num_sce,averages,SDs,averages_t,SDs_t)))
            print(mat_out[-1])
            #x_axis = [k for k in range(len(bs))]
            x_axis = [0,1,0,1,0,1]
            #ax.plot(x_axis,averages, color=cols[j])  
            #ax.fill_between(x_axis, min_vals, max_vals, color=cols[j], alpha=0.7)
            for k in range(6):
                if k in [0,1]:
                    fig, ax = fig1, ax1
                elif  k in [2,3]:
                    fig, ax = fig2, ax2
                elif  k in [4,5]:
                    fig, ax = fig3, ax3
                    
                if k in [0,2,4] :
                    ax.plot([x_axis[k]-bar_width , x_axis[k]+bar_width],[averages[k]]*2, color=cols[j] , label='Q=%i' %int(num_sce*lf))
                else:
                    ax.plot([x_axis[k]-bar_width , x_axis[k]+bar_width],[averages[k]]*2, color=cols[j])  
                ax.fill_between([x_axis[k]-bar_width , x_axis[k]+bar_width], [min_vals[k]]*2, [max_vals[k]]*2, color=cols[j], alpha=0.4)
            #ax.scatter(x_axis, averages,  color=cols[j] , label='Q=%i' %int(num_sce*lf)) 
           
            ave = np.array(averages)
            maxt = np.array(max_vals)
            mint = np.array(min_vals)
            sd = np.array(SDs)
            #print((maxt-mint)/ave)
            #print(np.mean((maxt-mint)/ave))
            variabilities.append(np.mean((maxt-mint)/ave))
        for k in range(3):
            if k == 0:
                fig, ax = fig1, ax1
                labx = params_def[[1,2]]
            elif k == 1:
                fig, ax = fig2, ax2
                labx = params_def[[3,4]]
            elif k == 2:
                fig, ax = fig3, ax3
                labx = params_def[[5,6]]
                
            ax.set_xlabel('Tightness parameters')
            #ax.set_xticklabels(params_def)
            ax.set_xticks([0,1])
            ax.set_xticklabels(labx)
            ax.set_ylabel('Pulse time (s)')
            ax.yaxis.set_minor_locator(AutoMinorLocator(5))
            
            ax.grid(which='minor', alpha=0.3)
            ax.grid(which='major', alpha=0.6)
            ax.legend(loc='best', shadow=True, fontsize='small')
            #ax.set_title('Sensitivity to the number of labels in instance RI%i' %(i))
            fig.tight_layout()
            #plt.tight_layout()
            pp = PdfPages('/Users/dduque/Dropbox/UNIANDES/Research/S-Pluse/R-CSP/Results/labels_sencitivity_RI%i_%i.pdf' %(i,k))
            pp.savefig(fig)
            pp.close()
                    
            
            
 
                
    

mat_out = np.array(mat_out)
col_names = ['Instance,', 'Q']
for w in [1,2,3]:
    for b in [1,2]:
        col_names.append('Pulse Mean (w%i,b%i)' %(w,b))
for w in [1,2,3]:
    for b in [1,2]:
        col_names.append('Pulse SD (w%i,b%i)' %(w,b))
for w in [1,2,3]:
    for b in [1,2]:
        col_names.append('Total Mean (w%i,b%i)' %(w,b))
for w in [1,2,3]:
    for b in [1,2]:
        col_names.append('Total SD (w%i,b%i)' %(w,b))
        
data_out = pd.DataFrame(data=mat_out,columns=col_names)            
data_out.to_csv('/Users/dduque/MacWorkspace/S-Pulse/RobustCSP/results/labelsVariabilityAnalysis2.csv')

data_out500 = data_out[data_out.Q==500]
data_out1000 = data_out[data_out.Q==1000]
data_out1500 = data_out[data_out.Q==1500]
data_out500.to_csv('/Users/dduque/MacWorkspace/S-Pulse/RobustCSP/results/labelsVariabilityAnalysis5002.csv')
data_out1000.to_csv('/Users/dduque/MacWorkspace/S-Pulse/RobustCSP/results/labelsVariabilityAnalysis10002.csv')
data_out1500.to_csv('/Users/dduque/MacWorkspace/S-Pulse/RobustCSP/results/labelsVariabilityAnalysis15002.csv')

print('Average variability ' , np.mean(variabilities), np.max(variabilities))


for (j,b) in enumerate(bs):
    fig, ax = plt.subplots()
    b_data = RIi[RIi.B==b]
    for (i,lf) in enumerate(lfs):
        lf_data =  b_data[b_data.labels_frac==lf]    
        ax.plot(list(lf_data.pulse_time), color=cols[i])