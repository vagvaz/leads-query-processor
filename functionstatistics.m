folder_name = uigetdir

csvs = dir([folder_name '/*.csv']);

for i=1:length(csvs)
   try
   data=csvread(csvs(i).name);
   disp([ ' records ' length(data) ' max: ' num2str(max(data)) ' min: ' num2str(min(data)) ' mean: ' num2str(mean(data)) ' std: ' num2str(std(data)) ' Term ' csvs(i).name ]) 
   figure;
   hist(data,50)
   title([listing(i).name ' ' num2str(length(data)) ' records'])
   xlabel('milliseconds');
   end
end