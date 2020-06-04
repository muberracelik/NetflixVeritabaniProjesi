MTFLİX 	Version 1.0	31/05/2020

						NETFLİX VERİTABANI PROJESİ

PROJE TANIMI:
--------------------------------------------------------------------------------------
	JAVA dili ve veri tabanı kullanılarak, kullanıcı odaklı dizi&film izleme platformu.
--------------------------------------------------------------------------------------

GELİŞTİRME ORTAMI:
--------------------------------------------------------------------------------------
	Proje Windows platformunda JAVA dili Swing kütüphanesi ve SQLite VTYS kullanılarak 
yazılmış olup; Netbeans ortamında, javac kullanılarak derlenip JVM aracılığı ile
test edilmiştir.
--------------------------------------------------------------------------------------

KULLANIM NOTLARI:
--------------------------------------------------------------------------------------

				    --HAZIRLIK--
0-	Uygulama jar dosyası ile çalıştırılır. Eğer bir geliştirme ortamı üzerinde
çalıştırılmak istenirse; Main metodu "Main.java" sınıfının içinde olup,
çalıştırmak için bu sınıf "main class" olarak belirlenmelidir. 

1-	Program verilerinin bulunduğu excel dosyasının ismi "Netflix DB.xlsx" olarak
değiştirilip ve database klasörünün içine atılmalıdır.
									
									
				   --OTURUM AÇMA--	
2-	Uygulama çalıştırıldığında "Login arayüzü" kullanıcıyı karşılar. Kullanıcı eğer 
daha önce kayıt olduysa e-posta ve şifresi ile giriş yapabilir. Kullanıcının girdiği
bilgiler veritabanındaki veriler ile eşleşiyorsa kullanıcının karşısına kullanıcının
program izleyebileceği yönetim arayüzü çıkar. Eğer kullanıcı veri tabanında kayıtlı
olmayan bir e-posta adresi girmişse kayıt arayüzüne yönlendirilir. Kullanıcı kayıt 
olmak isterse arayüzün en altında bulunan "Şimdi Aramıza Katıl" butonuna basarak
kayıt sayfasına gidebilir.


				     --KAYIT--
3-	Kayıt arayüzünde kullanıcıdan kayıt için gerekli bilgileri girmesi istenir.
Doğum tarihini girmek için ilgili bölümün sağ kısmında tarih seçici bir buton 
bulunmaktadır kullanıcı dilerse burdan kolay bir şekilde doğum tarihini seçebilir.
Kullanıcı, bilgileri eksik, hatalı veya var olan bir kullanıcının e-posta adresini
girerse uyarılır, aksi halde kullanıcı veri tabanına kayıt edilir ve sevdiği türleri
seçmesi için türlerin listelendiği bir ekran karşısına çıkar.

3.1-	Kullanıcı sevdiği 3 türü seçip Göster butonuna tıklayınca, kullanıcının
karşısına her türden 2'şer tane en yüksek puan alan program çıkar.

3.2-	Kullanıcı "Giriş Yap" butonuna basarak oturum açma ekranına dönebilir yada
programı kapatabilir. Kayıt işlemi tamamlandı.


			   --KULLANICI OTURUM YÖNETİMİ""
4-	Kullanıcı başarılı bir şekilde oturum açtıktan sonra karşısına kullanıcı yönetim 
arayüzü çıkar.

4.1-	İsme Göre Program Arama: Kullanıcı, yönetim arayüzünün sağ üstünde bulunan
bardan izlemek istediği programın ismini veya isminin bir kısmını text olarak girerek
istediği programı bulabilir.

4.2-	Türe Göre Program Arama: Kullanıcı, yönetim arayüzünün solunda bulunan bardan 
izlemek istediği türü seçerek o türe ait programları ve bilgilerini listeleyebilir.

4.3-	Program İzleme: 4.1 veya 4.2 deki işlemlerden sonra izlemek istediği programa 
tıklayınca; eğer programı daha önce izlemişse seçilen programa ait daha önceki izleme 
verilerin ve programı izleyebileceği başlat butonu, veyahut izlememişse yine o programa 
ait veriler ve programı izleyebileceği başlat butonu karşısına çıkar.

4.4 	Kullanıcı programı duraklatabilir, kaldığı yerden devam edebilir ve izlemeyi
sonlandırıp çıkabilir.

4.4	Kullanıcının programdan çıkması için çarpıya basması yeterlidir.

---------------------------------------------------------------------------------------


Müberra ÇELİK - muberraceliik@gmail.com
Taha Batuhan TÜRK - tbturkk@gmail.com
