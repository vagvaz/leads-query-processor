'''
Created on Apr 24, 2015

@author: nonlinear
'''
import sys
import re, string

if __name__ == '__main__':
    filename = sys.argv[1]
    
    with open(filename) as f:
        
        newpage = False
        newpagelines = []
        
        pattern = re.compile('[\W_]+')
        
        for line in f:
            #print line
            
            if not line.startswith('STDERR'):
                continue
            lps = re.split('[ :\t]',line)
            lps = filter(None, lps)
            #print lps
            
            title = None
            name  = None
            time  = None
            
            if len(lps)<4:
                continue
            
            ts = lps[1]+':'+lps[2]+':'+lps[3]
            
            # Beginning of a new page
            if len(lps)>7 and lps[5] == "Execution" and lps[6] == "time" and lps[7] == "of":
                title = "COMP"
                name  = lps[8].split('.')[-1]
                time  = lps[-2]
                
            elif len(lps)>5 and lps[5] == "LeadsQueryInterface.sendQuery()":
                if lps[8] == "'SELECT":
                    title = "SELECT"
                    name  = lps[11]
                else:
                    title = "INSERT"
                    name  = lps[10]
                name = pattern.sub('', name)
                time  = lps[-2].split(':')[-1]
                
            elif len(lps)>5 and lps[5] == "PythonQueueCall.call()":
                title = "PYTHON"
                name  = "..."
                time  = lps[-2]
                
            elif len(lps)>5 and lps[5] == "LeadsLuceneIndexingCall.call()":
                title = "LUCENE"
                name  = "---"
                time  = lps[-2]
            
            elif len(lps)>5 and lps[5] == "Plugin.created()":
                title = "PAGE"
                name  = lps[9]+":"+lps[10]
                time  = lps[-2]
            
            if title != None:
                print ts+','+title+','+name+','+time
            
            # TODO Odwroc kolejnosc
                
        # TODO zapisz do CSV!
                
                
                
                
                