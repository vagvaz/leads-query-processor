#!/bin/bash
set -o nounset
set -o errexit
set +x

LEADS_QUERY_ENGINE_CONTAINER_NAME=query_engine
export OS_AUTH_URL=https://identity-hamm5.cloudandheat.com:5000/v2.0

cpwd=${PWD} ;
echo $cpwd 

if [ $# -eq 0 ];
  then
    printf "No argument supplied\n\n"
    printf "Regarding user had alread execited LEADS-openrc.sh and \"source tools/openstack_cli/bin/activate\" the Supported arguments are:\n"
  	printf "list\t\t\t\t list all items in the container $LEADS_QUERY_ENGINE_CONTAINER_NAME\n"
  	printf "download <filename>\t\t try to download file from container\n"

  	printf "\nupload <filename>\t\t upload a file to the container, uploading fild in the root directory of the container\n"
  	printf "upload_with_dir <filename>\t upload a file to the container, uploading fild in the directory of the container as the files path in the argument\n"
  	
  	printf "uploadallmods\t\t\t find and upload all modules' zip files that exist under current folder, in the root directory of the container\n"
  	
  	printf "\ndelete <filename>\t\t try to delete file from container\n"
  	printf "\n\n"

  	printf "check env variables before uploading!!!, current URL: $OS_AUTH_URL\n"
  	exit 0
elif [ "$#" -eq 1 ]; then
	
	if [ $1 = "list" ]; then
		swift  --os-auth-url=${OS_AUTH_URL}  --os-username=${OS_USERNAME}  --os-password=${OS_PASSWORD}  --os-tenant-name=${OS_TENANT_NAME}  list -l  ${LEADS_QUERY_ENGINE_CONTAINER_NAME};

	elif [ $1 = "uploadallmods" ]; then
		#statements
		#echo `find * -name '*mod.zip' -type f `
		zipfiles=`find * -name '*mod.zip' -type f `
		#find * -name '*mod.zip' -type f
		#echo $zipfiles
		echo '' 
		for f in $zipfiles ; do
			##echo 'deleting ' $f
			#result=`swift   --os-auth-url=${OS_AUTH_URL}  --os-username=${OS_USERNAME} --os-password=${OS_PASSWORD}  --os-tenant-name=${OS_TENANT_NAME}  delete  ${LEADS_QUERY_ENGINE_CONTAINER_NAME}  $f`
			
			printf ' Getting into dir: '
	    	dirn=`dirname $f`
	    	printf $dirn
	    	printf ' trying to upload file: '
	    	fnn=`basename $f`
	    	printf $fnn
	    	# exit 0
			sleep 1
		    #swift   --os-auth-url=${OS_AUTH_URL}  --os-username=${OS_USERNAME} --os-password=${OS_PASSWORD}  --os-tenant-name=${OS_TENANT_NAME}  upload  --skip-identical --changed ${LEADS_QUERY_ENGINE_CONTAINER_NAME} $fnn;
			if [ $? -eq 0 ]
				then
				  echo " Successfully uploaded file"
				else
				  echo " Could not uploaded file" >&2
			fi
			
			cd $cpwd #necessary !
		done
		#| xargs -I {} -P 3  swift   --os-auth-url=${OS_AUTH_URL}  --os-username=${OS_USERNAME} --os-password=${OS_PASSWORD}  --os-tenant-name=${OS_TENANT_NAME}  upload  $LEADS_QUERY_ENGINE_CONTAINER_NAME {}
    fi
elif [ "$#" -eq 2 ]; then
	
	if [ $1 = "download" ]; then
		swift --info --os-auth-url=${OS_AUTH_URL}  --os-username=${OS_USERNAME}  --os-password=${OS_PASSWORD}  --os-tenant-name=${OS_TENANT_NAME}  download ${LEADS_QUERY_ENGINE_CONTAINER_NAME} $2 --skip-identical
	elif [ $1 = "delete" ]; then
		swift --os-auth-url=${OS_AUTH_URL}  --os-username=${OS_USERNAME}  --os-password=${OS_PASSWORD}  --os-tenant-name=${OS_TENANT_NAME}  delete ${LEADS_QUERY_ENGINE_CONTAINER_NAME} $2  
		if [ $? -eq 0 ]
			then
			  echo " Successfully deleted file"
			else
			  echo " Could not delete file" 
		fi
	elif [ $1 = "upload" ]; then

		if [ ! -f "$2" ]; then
        	echo "The file \"$2\" does not exist!"
        	exit 1
    	fi
    	printf ' Getting into dir: '
    	dirn=`dirname $2`
    	printf $dirn
    	printf ' trying to upload file: '
    	fnn=`basename $2`
    	printf $fnn
    
		swift --retries 10 --os-auth-url=${OS_AUTH_URL} --os-username=${OS_USERNAME} --os-password=${OS_PASSWORD}  --os-tenant-name=${OS_TENANT_NAME}  upload --skip-identical --changed ${LEADS_QUERY_ENGINE_CONTAINER_NAME} $fnn;
		if [ $? -eq 0 ]
			then
			  echo " Successfully uploaded file : $2"
			else
			  echo " Could not uploaded file" >&2
		fi
		cd $cpwd #unnecessary 
	

	elif [ $1 = "upload_with_dir" ]; then

		if [ ! -f "$2" ]; then
        	echo "The file \"$2\" does not exist!"
        	exit 1
    	fi

		swift --retries 10 --os-auth-url=${OS_AUTH_URL} --os-username=${OS_USERNAME} --os-password=${OS_PASSWORD}  --os-tenant-name=${OS_TENANT_NAME}  upload --skip-identical --changed ${LEADS_QUERY_ENGINE_CONTAINER_NAME} $2;
		if [ $? -eq 0 ]
			then
			  echo " Successfully uploaded file : $2"
			else
			  echo " Could not uploaded file" >&2
		fi
		cd $cpwd #unnecessary 
	fi

fi
