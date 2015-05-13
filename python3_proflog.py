#!/usr/bin/python3
#use ONLY PYTHON3
import sys, getopt
import re
import statistics
def main(argv):
	inputfile = ''
	outputfile = ''
	try:
		opts, args = getopt.getopt(argv,"hi:o:",["ifile=","ofile="])
	except getopt.GetoptError:
		print('process_proflog.py -i <inputfile> -o <outputfile>')
		sys.exit(2)
		
	for opt, arg in opts:
		if opt == '-h':
			print('test.py -i <inputfile> -o <outputfile>')
			sys.exit()
		elif opt in ("-i", "--ifile"):
			inputfile = arg
		elif opt in ("-o", "--ofile"):
			outputfile = arg
	if(inputfile==''):
		print('Incorrect arguments, process_proflog.py  -i <inputfile> -o <outputfile>')
		sys.exit(2)
	print('Input file is "', inputfile,'"')
	if(outputfile==''):
		outputfile=inputfile+'.csv'
	print('Output file is "', outputfile,'"')
	

	infile = open(inputfile) #grep #PROF leadsprocessor.log 

	searchTerms = []
	searchTerms.append('findPendingMMCFromGlobal()');
	searchTerms.append('findPendingRMCFromGlobal()');
	searchTerms.append('createCaches()');
	searchTerms.append('setupMapCallable()');
	searchTerms.append('executeMap()');
	searchTerms.append('ActualExecMap');
	searchTerms.append('setupReduceCallable()');
	searchTerms.append('executeReduce()');
	searchTerms.append('run_cleanup');
	searchTerms.append('fail_cleanup');
	
	searchTerms.append('GetTuple');
	searchTerms.append('putToCache');
	searchTerms.append('ExOn');
	searchTerms.append('new Tuple');
	searchTerms.append('namesToLowerCase');
	searchTerms.append('prepareOutput');
	searchTerms.append('tree.accept');
	searchTerms.append('Scan_outputToCache');
	searchTerms.append('Scan_Put');
	
	searchTerms.append('Start EnsemlbeCacheManager');
	searchTerms.append('Get cache');

	searchTerms.append('Call getComponent');
	searchTerms.append('finalize');
	searchTerms.append('renameAllTupleAttributes');
	searchTerms.append('ScanCallableUpdate -> setEnv');
	searchTerms.append('Start EnsemlbeCacheManager');
	searchTerms.append('end_setEnv');
	searchTerms.append('Op CleanUp');
	searchTerms.append('Execute()');


	count=[];
	variables=[];
	sums=[];
	means=[];
	medians=[];
	minimum=[];
	maximum=[];
	#create file
	fout=open(outputfile, 'w+')
	for i in range(len(searchTerms)):
		
		count.append(0)
		variables.append([])
		sums.append(0)
		medians.append(float('nan'))
		means.append(float('nan'))
		minimum.append(float('inf') )
		maximum.append(float('-inf') )
		
	lines_red=0;
	#print(variables)
	mod=1000;
	for line in infile:
		line = line.rstrip()
		
		for i in range(len(searchTerms)):
			if re.search(searchTerms[i], line) :
				#line_ = line.split(" ")
				lasts = line.split('\t') 
				#print lasts
				if(len(lasts)>1):
					ms = lasts[-1].split(' ')[0]
				else:
					llaast = lasts[-1].split(' ')
					ms = llaast[-2]
				val = float(ms)
				if(val<minimum[i]):
					minimum[i]=val
				if(val>maximum[i]):
					maximum[i]=val
				sums[i]=sums[i]+val
				#print ms
				#files[i].write(ms+'\n')
				variables[i].append(val)
				count[i]=count[i]+1
				break
		lines_red+=1
		if(lines_red%mod==0):
			print('Processed lines '+str(lines_red))
			if(lines_red>4*mod):
				mod*=2;

	for i in range(len(searchTerms)):
		if(count[i]>0):
			means[i] = statistics.mean(variables[i])
			medians[i] = statistics.median(variables[i])
			
	line = 'logSearchTerms,records,max,min,mean,std,median,sum'
	fout.write(line);
	fout.write('\n');
	for i in range(len(searchTerms)):
		line = searchTerms[i]+','+ str(round(count[i],5))+','+str(round(maximum[i],5))+','+str(round(minimum[i],5))+','+str(round(means[i],5))+','+str(round(medians[i],5))+','+str(round(sums[i],5))+'\n'
		print(str(count[i]) +'\tfound for term '+line) 
		fout.write(line);
		#files[i].close()
		#print(line)
	fout.close();
	print('Processing completed, processed ' + str(lines_red) + ' log file line');

if __name__ == "__main__":
	main(sys.argv[1:])
