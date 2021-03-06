'''
Created on Jan 22, 2014

@author: nonlinear
'''

import io
from lxml import html
from BeautifulSoup import BeautifulSoup
from eu.leads.infext.python.ops.xpathops import content2tree

class VirtualClassifier:
    '''
    classdocs
    '''

    def __init__(self):
        pass
                
    def classify(self,page_dict,params=None):
        self.certainty = None
        self.nodepath = None
        self.features = None
        
        retval = None
        
        if page_dict.get("tree")==None:
            page_dict["tree"] = content2tree(page_dict.get("content"))
            
        self.page_dict = page_dict
            
        if self.find(params):
            retval = True
            
#        page_dict["tree"] = None
        
        return retval
    
    
    def getNodePath(self):
        return self.nodepath
   
    def getCertainty(self):
        return self.certainty
    
    def getFeaturesVals(self):
        return self.features
    
    def find(self,params):
        pass    
    
    