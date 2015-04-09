#!/bin/bash
set -o nounset
set -o errexit
set +x

LEADS_QUERY_ENGINE_CONTAINER_NAME=query_engine
export OS_AUTH_URL=https://identity-hamm5.cloudandheat.com:5000/v2.0

if [ $# -eq 0 ];
  then
    printf "No argument supplied\n\n"
    printf "Regarding user had alread execited LEADS-openrc.sh and \"source tools/openstack_cli/bin/activate\" the Supported arguments are:\n"
  	printf "list\t\t\t list all items in the container $LEADS_QUERY_ENGINE_CONTAINER_NAME\n"
  	printf "download <filename>\t try to download file from container\n"
  	printf "upload <filename>\t upload a file to the container\n"
  	printf "uploadallmods\t\t find and upload all modules' zip files that exist under current folder\n"
  	printf "\n\n"

  	printf "check env variables before uploading!!!, current URL: $OS_AUTH_URL\n"
  	exit 1
elif [ "$#" -eq 1 ]; then
	
	if [ $1 = "list" ]; then
		swift --os-auth-url=${OS_AUTH_URL}  --os-username=${OS_USERNAME}  --os-password=${OS_PASSWORD}  --os-tenant-name=${OS_TENANT_NAME}  list ${LEADS_QUERY_ENGINE_CONTAINER_NAME};
	elif [ $1 = "uploadallmods"]; then
			#statements
			find * -name '*mod.zip' -type f | xargs -I {} -P 3  swift   --os-auth-url=${OS_AUTH_URL} \
		     --os-username=${OS_USERNAME} \
		     --os-password=${OS_PASSWORD} \
		     --os-tenant-name=${OS_TENANT_NAME} \
		     upload --skip-identical --changed $LEADS_QUERY_ENGINE_CONTAINER_NAME {}
	fi
elif [ "$#" -eq 2 ]; then
	
	if [ $1 = "download" ]; then
		swift --os-auth-url=${OS_AUTH_URL}  --os-username=${OS_USERNAME}  --os-password=${OS_PASSWORD}  --os-tenant-name=${OS_TENANT_NAME}  download ${LEADS_QUERY_ENGINE_CONTAINER_NAME} $2 --skip-identical
	elif [ $1 = "upload" ]; then
		if [ ! -f "$2" ]; then
        	echo "The file \"$2\" does not exist!"
        	exit 1
    	fi
		swift  --os-auth-url=${OS_AUTH_URL} --os-username=${OS_USERNAME} --os-password=${OS_PASSWORD}  --os-tenant-name=${OS_TENANT_NAME}  upload --skip-identical --changed ${LEADS_QUERY_ENGINE_CONTAINER_NAME} $2
	fi
fi