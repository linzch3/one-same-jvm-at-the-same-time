bin=$(cd $(dirname $0);pwd)
java -classpath ${bin}/target/classes CheckByPort 2>&1 &
java -classpath ${bin}/target/classes CheckByPort