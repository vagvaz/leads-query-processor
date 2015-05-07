folder_name = uigetdir

csvs = dir([folder_name '/*.csv']);
%printmat(M, 'My Matrix', 'ROW1 ROW2 ROW3 ROW4 ROW5', 'FOO BAR BAZ BUZZ FUZZ' )
stats=[];

vars = ['records max min  mean std sum'];
terms = {};
 

%    disp([ ' records ' length(data) ' max: ' num2str(max(data)) ' min: ' num2str(min(data)) ' mean: ' num2str(mean(data)) ' std: ' num2str(std(data)) ' Term ' csvs(i).name ]) 
k = strsplit(folder_name,'/');
c=0;
while(exist([folder_name '/'  'statistics_' k{end}  '_' num2str(c) '.CSV' ],'file'))
    c=c+1;
end

fileID = fopen([folder_name '/'  'statistics_' k{end}  '_' num2str(c) '.CSV' ] ,'w');

fprintf(fileID,'term,records,max,min,mean,std,sum\n');

for i=1:length(csvs)
   try
       csvs(i).name = csvs(i).name;
       data=csvread([folder_name '/' csvs(i).name]);
       %disp([ ' records ' length(data) ' max: ' num2str(max(data)) ' min: ' num2str(min(data)) ' mean: ' num2str(mean(data)) ' std: ' num2str(std(data)) ' Term ' csvs(i).name ]) 
       stats(end+1,:) = [length(data) max(data) min(data) mean(data) std(data) sum(data)];
       terms{end+1} = strrep(strrep(csvs(i).name,'.csv',''),' ','_'); 
       
       fprintf(fileID,'%s,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f\n',terms{end},stats(end,1),stats(end,2),stats(end,3),stats(end,4),stats(end,5),stats(end,6));
       figure;
       hist(data,50)
       title([csvs(i).name ' ' num2str(length(data)) ' records'])
       xlabel('milliseconds');
   end
end

fclose(fileID);
Allterms=[];
for i = 1:length(terms)
    Allterms=[Allterms ' ' terms{i}];
end

printmat(stats, 'Statistics', Allterms, vars )
