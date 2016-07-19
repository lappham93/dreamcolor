for f in $( ls /sources/java-projects/ | grep dreamcolor ); do
        if [ -e /sources/java-projects/$f/runservice ]; then
                cd /sources/java-projects/$f
                sh ./runservice restart development;
        fi;
done
