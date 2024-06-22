# Planivent
Este o aplicaţie pentru gestionarea serviciilor în domeniul organizării evenimentelor pe care o puteţi găsi accesând link-ul: https://github.com/DanaMarinescu/Planivent.
# Cerinţe preliminare
# Descărcare instrumente necesare
Pentru rularea aplicaţiei este necesară instalarea [GitKraken](https://www.gitkraken.com/) sau [Git](https://git-scm.com/),   [Java JDK 19](https://www.oracle.com/java/technologies/javase/jdk19-archive-downloads.html) , [Maven](https://maven.apache.org/), [Node JS](https://nodejs.org/en/download/prebuilt-installer) şi [IntelliJ IDEA](https://lp.jetbrains.com/intellij-idea-features-promo/?msclkid=ad51a29d872c1b511674c4eaa697eab7&utm_source=bing&utm_medium=cpc&utm_campaign=EMEA_en_EAST_IDEA_Branded&utm_term=intellij%20IDEA&utm_content=intellij%20idea) ,urmând paşii descrişi în documentaţiile oficiale.
# Deschiderea proiectului
Paşii pentru deschiderea şi rularea proiectului sunt următorii:
1. Clonarea repository-ului folosind Git sau GitKraken în folderul ce va găzdui aplicaţia, cu ajutorul comenzii
```git
git clone https://github.com/DanaMarinescu/Planivent.git
```
2. Deschideţi IntelliJ IDEA
3. Importaţi proiectul prin selectarea "File" -> "Open" -> navigare către directorul unde a fost clonat proiectul şi selectarea lui. Confirmaţi deschiderea.
4. Configuraţi JDK 19 în IntelliJ IDEA accesând "File" -> "Project Structure..." -> "SDKs" -> setare cale către JDK 19 la "JDK home path"
```git
path/to/jdk-19
```
5.Configuraţi Maven în IntelliJ IDEA accesând "File" -> "Project Structure..." -> "Libraries" -> "+" şi alegeţi "from Maven".
# Rularea aplicaţiei
1. Construiţi proiectul
```git
mvn clean install
```
2. Rulaţi aplicaţia
```git
mvn spring-boot:run
```
