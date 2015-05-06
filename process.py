import re
hand = open('selectWithData.txt') #grep #PROF leadsprocessor.log 

searchTerms = []
searchTerms.append('findPendingMMCFromGlobal');
searchTerms.append('findPendingRMCFromGlobal');
searchTerms.append('createCaches');
searchTerms.append('setupMapCallable');
searchTerms.append('executeMap');
searchTerms.append('setEnv');

searchTerms.append('setupReduceCallable');
searchTerms.append('executeReduce');
searchTerms.append('run_cleanup');
searchTerms.append('fail_cleanup');
searchTerms.append('fail_cleanup');
searchTerms.append('Scan_outputToCache');

searchTerms.append('Call getComponent');
searchTerms.append('GetTuple');
searchTerms.append('Start EnsemlbeCacheManager');
searchTerms.append('Get cache');
searchTerms.append("new Tuple");
searchTerms.append("namesToLowerCase");
searchTerms.append("Scan_Put");
searchTerms.append('prepareOutput');
searchTerms.append('renameAllTupleAttributes');
searchTerms.append('ExOn');

files=[];
count=[];
#create files
for i in range(len(searchTerms)):
	filename = searchTerms[i]+'.csv'
	#print filename
	f1=open(filename, 'w+')
	files.append(f1);
	#f1.write(searchTerms[i]+'\n')
	count.append(0)

for line in hand:
	line = line.rstrip()
	for i in range(len(searchTerms)):
		if re.search(searchTerms[i], line) :
			#line_ = line.split(" ")
			lasts = line.split('\t') 
			ms = lasts[-1].split(' ')[0]
			#print ms
			files[i].write(ms+'\n')
			count[i]=count[i]+1


for i in range(len(searchTerms)):
	print str(count[i]) +'\tfound for term ' + searchTerms[i] 
	files[i].close()


# for line in open("file"):
#  if "search_string" in line:
#    print line
