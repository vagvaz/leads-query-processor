folder_name = uigetdir

csvs = dir([folder_name '/*.csv']);

for i=1:length(csvs)
   try
   data=csvread(csvs(i).name);
   disp([ ' records ' length(data) ' max: ' num2str(max(data)) ' min: ' num2str(min(data)) ' mean: ' num2str(mean(data)) ' std: ' num2str(std(data)) ' sum  '  num2str(sum(data)) ' Term ' csvs(i).name ]) 
   figure;
   hist(data,50)
   title([csvs(i).name ' ' num2str(length(data)) ' records'])
   xlabel('milliseconds');
   
%filename =  strcat(csvs(i).name , 'my.eps')
%cmd = ['print -deps ',filename]; % .png graphics format
% For monochrome PostScript format: 
%     cmd = ['print -deps ',filename];
% For color PostScript format: 
%     cmd = ['print -depsc ',filename];
% Etc. - say 'help print' in either Matlab or Octave
%disp(cmd); eval(cmd);
   end
end
