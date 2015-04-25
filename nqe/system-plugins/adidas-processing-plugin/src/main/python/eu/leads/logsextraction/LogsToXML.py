'''
Created on Apr 24, 2015

@author: nonlinear
'''
import sys
import re

if __name__ == '__main__':
    filename = sys.argv[1]
    
    with open(filename) as f:
        
        newpage = False
        newpagelines = []
        
        for line in f:
            #print line
            
            lps = re.split('[ :\t]',line)
            lps = filter(None, lps)
            #print lps
            
            if len(lps)<3:
                continue
            
            
            # Beginning of a new page
            if lps[4]=="Processing":
                newpage = True
                continue
            
            elif newpage == True and lps[4] == "+++":
                
                if lps[5] == "Execution" and lps[6] == "time" and lps[7] == "of":
                    title = "COMP"
                    name  = lps[8].split('.')[-1]
                    time  = lps[-2]
                    
                elif lps[5] == "LeadsQueryInterface.sendQuery()":
                    if lps[8] == "'SELECT":
                        title = "SELECT"
                        name  = lps[11]
                    else:
                        title = "INSERT"
                        name  = lps[10]
                    time  = lps[-2].split(':')[-1]
                    
                elif lps[5] == "PythonQueueCall.call()":
                    title = "PYTHON"
                    name  = "..."
                    time  = lps[-2]
                
                elif lps[5] == "Plugin.created()":
                    title = "PAGE"
                    name  = lps[8]
                    time  = lps[-2]
                
                print title, name, time
                
                # TODO Odwroc kolejnosc
                
        # TODO zapisz do CSV!
                
                
                
                
                