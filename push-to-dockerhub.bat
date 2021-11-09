docker build -t lgaljo/rt_basketball_notifications -f Dockerfile_with_maven_build .
docker tag lgaljo/rt_basketball_notifications lgaljo/rt_basketball_notifications:latest
docker push -a lgaljo/rt_basketball_notifications