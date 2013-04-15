#
# Copyright (C) 2013 Julian Atienza Herrero <j.atienza at har.mrc.ac.uk>
#
# MEDICAL RESEARCH COUNCIL UK MRC
#
# Harwell Mammalian Genetics Unit
#
# http://www.har.mrc.ac.uk
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not
# use this file except in compliance with the License. You may obtain a copy of
# the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations under
# the License.
#

generate_project () 
{


	mvn archetype:generate -B -DarchetypeGroupId=org.mousephenotype.dcc.base-archetype \
                            -DarchetypeArtifactId=base-archetype \
                            -DarchetypeVersion=1.0.0-SNAPSHOT \
                            -DarchetypeRepository=http://193.63.70.36:8080/nexus-2.0.5/content/groups/public/ \
                            -DgroupId=org.mousephenotype.dcc.$1\
                            -DartifactId=$1 \
                            -Dversion=1.0.0-SNAPSHOT


}


#mkdir ../utils.xml

#mkdir ../utils.persistence

mkdir ../utils.io

#generate_project 'utils.xml'

#generate_project 'utils.persistence'

generate_project 'utils.io'

#mv utils.xml ../.

#mv  utils.persistence ../.

mv utils.io ../.
