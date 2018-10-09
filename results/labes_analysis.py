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

os.chdir('/Users/dduque/Dropbox/WORKSPACE/S-Pulse/RobustCSP/results')
filepath = './RN_results_labels.csv'
data = pd.read_csv(filepath)
test = data[(data.instance=='RI7') & (data.scenarios==100) &(data.replica==0) &(data.labels_frac==0.5)]


params_def = ['b%i/w%i' %(b,w) for b in [1,2] for w in [1,2,3]]
params_def.insert(0,'')

cols = ['red', 'blue', 'green', 'purple', 'yellow']
num_sce = 1000
for i in range(1,31):
    RIi = data[(data.instance=='RI%i' %(i)) & (data.scenarios==num_sce)]
    Ws = np.unique(RIi.W)
    bs = np.unique(RIi.B)
    lfs = np.unique(RIi.labels_frac)
    lfs = lfs[lfs<=1.5]
    if RIi.pulse_time.mean() > 10 and i!=9:
        print('Doing plot for %i' %(i))
        fig, ax = plt.subplots()
        for (j,lf) in enumerate(lfs):
            lf_data = RIi[RIi.labels_frac==lf]
            averages = [lf_data[lf_data.B==b].mean()['pulse_time'] for b in bs]
            max_vals = [lf_data[lf_data.B==b].max()['pulse_time'] for b in bs]
            min_vals = [lf_data[lf_data.B==b].min()['pulse_time'] for b in bs]
            x_axis = [k for k in range(len(bs))]
#            for (k,lf) in enumerate(lfs):
#                #ax.plot([k],[averages[k]], color=cols[i])  
#                ax.fill_between([x_axis[k]], [min_vals[k]], [max_vals[k]], color=cols[j], alpha=0.9)
            ax.scatter(x_axis, averages,  color=cols[j] , label='Q=%i' %int(num_sce*lf))  
        ax.set_xlabel('Parameters tightness (b/w)')
        ax.set_xticklabels(params_def)
        ax.set_ylabel('Pulse time (s)')
        ax.legend(loc='best', shadow=True, fontsize='small')
        ax.set_title('Sensitivity to the number of labels in instance RI%i' %(i))
    
for (j,b) in enumerate(bs):
    fig, ax = plt.subplots()
    b_data = RIi[RIi.B==b]
    for (i,lf) in enumerate(lfs):
        lf_data =  b_data[b_data.labels_frac==lf]    
        ax.plot(list(lf_data.pulse_time), color=cols[i])