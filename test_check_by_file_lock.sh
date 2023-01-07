bin=$(cd $(dirname $0);pwd)
java -classpath ${bin}/target/classes CheckByFileLock 2>&1 &
java -classpath ${bin}/target/classes CheckByFileLock