'''
Created on Feb 5, 2014

@author: nonlinear
'''
from eu.leads.infext.python.lang.dict_en import dict_en
from eu.leads.infext.python.lang.dict_de import dict_de

class LangDicts:
    
    def __init__(self):
        self.dicts = {"en":dict_en,
                      "de":dict_de}
        
    def get(self,dict):
        if dict in self.dicts:
            return self.dicts.get(dict)
        else:
            return self.dicts.get("en")

lang_dicts = LangDicts()
        