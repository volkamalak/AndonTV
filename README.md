# Andon TV - Elyaf Açma Makinesi İzleme Uygulaması

Bu uygulama, elyaf açma makineleri için Andon TV sistemidir. İki kazanlı sistem üzerinde çalışır ve operatörlere gerçek zamanlı iş emri bilgilerini gösterir.

## Özellikler

- **İki Kazanlı Görünüm**: Sol ve sağ kazan için ayrı ayrı iş emri bilgileri
- **Aktif Kazan Göstergesi**: Çalışan kazan yeşil renkte vurgulanır
- **Gerçek Zamanlı Güncelleme**: 30 saniyede bir otomatik veri yenileme
- **IP Bazlı Makine Tanıma**: TV'nin IP adresine göre hangi makinenin verisini göstereceği belirlenir
- **Tedarikçi Bilgileri**: Her iş emri için tedarikçi listesi
- **Balya Takibi**: Balya sayısı, toplam ağırlık, son balya numarası
- **Vardiya Bilgisi**: Mevcut vardiya ve vardiyada okutulan balya sayısı
- **7/24 Çalışma**: Ekran sürekli açık kalır, uygulama kapanmaz

## Kurulum

### 1. Proje Dosyalarını Android Studio'ya Aktarma

1. Android Studio'yu açın
2. "Open an Existing Project" seçeneğini seçin
3. `AndonTV` klasörünü seçin ve "OK" butonuna tıklayın

### 2. Backend URL Yapılandırması

`app/src/main/java/com/isletme/andontv/api/RetrofitClient.kt` dosyasını açın ve `BASE_URL` değerini kendi backend sunucunuzun URL'si ile değiştirin:

```kotlin
private const val BASE_URL = "http://YOUR_BACKEND_SERVER_IP:PORT/"
```

Örnek:
```kotlin
private const val BASE_URL = "http://192.168.1.100:8080/"
```

### 3. Backend API Beklenen Veri Formatı

Backend servisinizin `GET /api/machine-data?ip={tvIpAddress}` endpoint'inde aşağıdaki JSON formatında veri döndürmesi gerekir:

```json
{
  "machineName": "ELYAF AÇMA MAKİNESİ 1",
  "machineIp": "192.168.1.100",
  "currentShift": "1",
  "timestamp": 1733579445000,
  "leftKazan": {
    "workOrderNumber": "WO-12345",
    "isActive": true,
    "suppliers": [
      {
        "supplierName": "ABC Tekstil",
        "supplierCode": "ABC001"
      }
    ],
    "baleCount": 45,
    "totalWeight": 1250.5,
    "lastBaleNumber": "B-00045",
    "shiftBaleCount": 12
  },
  "rightKazan": {
    "workOrderNumber": "WO-12346",
    "isActive": false,
    "suppliers": [
      {
        "supplierName": "XYZ Pamuk",
        "supplierCode": "XYZ001"
      },
      {
        "supplierName": "DEF Elyaf",
        "supplierCode": "DEF001"
      }
    ],
    "baleCount": 38,
    "totalWeight": 980.3,
    "lastBaleNumber": "B-00038",
    "shiftBaleCount": 8
  }
}
```

**Notlar:**
- Eğer bir kazanda iş emri yoksa, `leftKazan` veya `rightKazan` değerini `null` olarak gönderebilirsiniz
- `isActive` alanı `true` olan kazan yeşil renkte gösterilir
- `suppliers` array'i birden fazla tedarikçi içerebilir
- `timestamp` milisaniye cinsinden Unix timestamp formatındadır

### 4. TV'ye Sabit IP Atama

Android TV veya tablet cihazınıza sabit IP adresi atayın:

1. **Ayarlar** > **Network & Internet** > **Wi-Fi** veya **Ethernet**
2. Bağlı olduğunuz ağa tıklayın
3. **IP Settings** > **Static**
4. IP adresini, gateway ve DNS ayarlarını yapın

### 5. Uygulamayı Çalıştırma

1. Android TV/Tablet cihazınızı USB ile bilgisayara bağlayın veya kablosuz debugging'i etkinleştirin
2. Android Studio'da cihazınızı seçin
3. "Run" butonuna basın (Shift + F10)

## Teknik Detaylar

### Kullanılan Teknolojiler

- **Kotlin**: Ana programlama dili
- **MVVM Architecture**: ViewModel ile veri yönetimi
- **Retrofit**: REST API entegrasyonu
- **Coroutines**: Asenkron işlemler
- **LiveData**: Reaktif UI güncellemeleri
- **Material Design**: Modern UI bileşenleri
- **CardView**: Bilgi kartları

### Mimari

```
app/
├── api/                    # Retrofit API servisleri
│   ├── AndonApiService.kt
│   └── RetrofitClient.kt
├── model/                  # Veri modelleri
│   ├── MachineData.kt
│   ├── WorkOrder.kt
│   └── Supplier.kt
├── utils/                  # Yardımcı sınıflar
│   ├── NetworkUtils.kt
│   └── DateTimeUtils.kt
├── viewmodel/              # ViewModel sınıfları
│   └── AndonViewModel.kt
└── MainActivity.kt         # Ana Activity
```

### Ekran Yapısı

```
┌─────────────────────────────────────────────────────────┐
│  MAKINE ADI                              IP: 192.168...  │
├───────────────────────┬─────────────────────────────────┤
│   SOL KAZAN          │      SAĞ KAZAN                   │
│                      │                                   │
│  İŞ EMRİ: WO-12345   │   İŞ EMRİ: WO-12346             │
│                      │                                   │
│  ┌────────────────┐  │   ┌────────────────┐            │
│  │ Tedarikçi      │  │   │ Tedarikçi      │            │
│  │ ABC Tekstil    │  │   │ XYZ Pamuk      │            │
│  └────────────────┘  │   │ DEF Elyaf      │            │
│  ┌────────────────┐  │   └────────────────┘            │
│  │ Balya Sayısı   │  │   ┌────────────────┐            │
│  │ 45 adet        │  │   │ Balya Sayısı   │            │
│  └────────────────┘  │   │ 38 adet        │            │
│  ...                 │   └────────────────┘            │
│                      │   ...                            │
├───────────────────────┴─────────────────────────────────┤
│  Vardiya: 1                     07.12.2024  14:30:45    │
└─────────────────────────────────────────────────────────┘
```

## Sorun Giderme

### Uygulama veri göstermiyor

1. Backend sunucusunun çalıştığından emin olun
2. TV ve backend sunucu aynı network'te olmalı
3. Backend URL'nin doğru olduğunu kontrol edin (`RetrofitClient.kt`)
4. Android Studio'daki Logcat'te hata mesajlarını kontrol edin

### IP adresi "IP Bulunamadı" gösteriyor

1. TV/Tablet'in Wi-Fi veya Ethernet bağlantısını kontrol edin
2. Ağ izinlerinin verildiğinden emin olun
3. Cihazı yeniden başlatın

### Ekran kapanıyor

1. `AndroidManifest.xml` dosyasında `WAKE_LOCK` izninin olduğundan emin olun
2. Cihazın güç tasarrufu ayarlarını kontrol edin
3. Uygulama için "Optimizasyonları Yoksay" seçeneğini etkinleştirin

## Lisans

Bu proje şirket içi kullanım için geliştirilmiştir.

## İletişim

Sorularınız için IT departmanı ile iletişime geçin.
