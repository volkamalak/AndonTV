# Uygulama İkonu Kurulum Rehberi

Uygulama için geçici olarak vector drawable ikonlar oluşturuldu. Ancak daha profesyonel görünüm için Android Studio'nun **Image Asset Studio** aracını kullanmanız önerilir.

## Seçenek 1: Mevcut Vector İkonları Kullanma (Hızlı Çözüm)

Şu anda vector drawable ikonlar hazır ve çalışıyor:
- `drawable/ic_launcher_background.xml` - Arka plan (mavi)
- `drawable/ic_launcher_foreground.xml` - Ön plan (TV ve fabrika ikonu)
- `mipmap-anydpi-v26/ic_launcher.xml` - Android 8.0+ için adaptive icon
- `mipmap-anydpi-v26/ic_launcher_round.xml` - Yuvarlak ikon

Bu ikonlar Android 8.0+ (API 26+) cihazlarda çalışacaktır.

## Seçenek 2: Android Studio Image Asset Studio Kullanma (Önerilen)

Profesyonel, tüm çözünürlüklerde optimize edilmiş ikonlar oluşturmak için:

### Adım 1: Image Asset Studio'yu Açın

1. Android Studio'da projeyi açın
2. Sol taraftaki **Project** görünümünde `app` klasörüne sağ tıklayın
3. **New** > **Image Asset** seçeneğini seçin

### Adım 2: İkon Tipini Seçin

**Launcher Icons (Adaptive and Legacy)** seçili olmalı (varsayılan)

### Adım 3: İkon Tasarlayın

İki yöntem var:

#### Yöntem A: Clipart Kullanma (Hızlı)

1. **Foreground Layer** sekmesinde
2. **Asset Type**: **Clip Art** seçin
3. Clip Art ikonuna tıklayın ve arama yapın:
   - "tv" - TV ikonu için
   - "factory" - Fabrika ikonu için
   - "monitor" - Monitör ikonu için
   - "display" - Ekran ikonu için
4. **Resize**: %70-80 arası ayarlayın
5. **Color**: Beyaz (#FFFFFF) seçin

#### Yöntem B: Kendi Görselinizi Kullanma

1. **Asset Type**: **Image** seçin
2. **Path**: Kendi logo/görsel dosyanızı seçin (PNG, JPG, SVG)
3. **Resize**: Gerektiği kadar ayarlayın

### Adım 4: Arka Plan Rengini Ayarlayın

1. **Background Layer** sekmesine geçin
2. **Asset Type**: **Color** seçin
3. **Color**: `#1976D2` (mavi) veya şirket renginizi seçin

### Adım 5: Önizleme ve Oluştur

1. Sağ tarafta tüm boyutlarda önizleme göreceksiniz
2. **Name**: `ic_launcher` olarak bırakın (değiştirmeyin)
3. **Next** butonuna tıklayın
4. **Finish** butonuna tıklayın

### Adım 6: Eski Dosyaları Değiştir

Image Asset Studio otomatik olarak tüm gerekli dosyaları oluşturacak ve eskilerinin üzerine yazacak:
- Tüm mipmap klasörlerinde PNG dosyaları (mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi)
- Adaptive icon XML dosyaları (API 26+)

## Seçenek 3: Kendi PNG Dosyalarınızı Kullanma

Eğer hazır PNG ikonlarınız varsa, bunları manuel olarak ekleyebilirsiniz:

### Gerekli Boyutlar:

Her klasöre uygun boyutta `ic_launcher.png` ve `ic_launcher_round.png` ekleyin:

```
res/
├── mipmap-mdpi/
│   ├── ic_launcher.png (48x48 px)
│   └── ic_launcher_round.png (48x48 px)
├── mipmap-hdpi/
│   ├── ic_launcher.png (72x72 px)
│   └── ic_launcher_round.png (72x72 px)
├── mipmap-xhdpi/
│   ├── ic_launcher.png (96x96 px)
│   └── ic_launcher_round.png (96x96 px)
├── mipmap-xxhdpi/
│   ├── ic_launcher.png (144x144 px)
│   └── ic_launcher_round.png (144x144 px)
└── mipmap-xxxhdpi/
    ├── ic_launcher.png (192x192 px)
    └── ic_launcher_round.png (192x192 px)
```

### PNG Oluşturma Araçları:

- **Online**: https://romannurik.github.io/AndroidAssetStudio/
- **Photoshop/GIMP**: Yukarıdaki boyutlarda manuel oluşturun
- **Figma/Sketch**: Export özelliğini kullanın

## Önerilen Tasarım İpuçları

1. **Basit Tutun**: İkonlar küçük boyutlarda görüneceği için detaylı grafiklerden kaçının
2. **Kontrast Kullanın**: Ön plan ve arka plan arasında yeterli kontrast olsun
3. **Şirket Kimliği**: Şirketinizin marka renklerini kullanın
4. **Test Edin**: Farklı launcher'larda (Ana ekran uygulamaları) test edin:
   - Yuvarlak
   - Kare
   - Squircle (yuvarlatılmış kare)
   - Teardrop

## Sorun Giderme

### "Default icon" veya Android robot ikonu görünüyor

1. Projeyi temizleyin: **Build** > **Clean Project**
2. Rebuild edin: **Build** > **Rebuild Project**
3. Uygulamayı tamamen silin ve yeniden yükleyin

### İkon bulanık görünüyor

- Daha yüksek çözünürlüklü kaynak görsel kullanın
- Image Asset Studio'da **Resize** ayarını düşürün

### Adaptive icon çalışmıyor

- Android 8.0+ (API 26+) cihaz olduğundan emin olun
- `mipmap-anydpi-v26` klasöründeki XML dosyalarını kontrol edin

## Hızlı Test

İkonu test etmek için:

1. Uygulamayı cihaza yükleyin
2. Ana ekranda uygulama ikonunu kontrol edin
3. Uygulama çekmecesinde (app drawer) kontrol edin
4. Son uygulamalar ekranında kontrol edin

## Sonuç

Mevcut vector drawable ikonlar geçici olarak çalışacaktır. Profesyonel bir görünüm için **Android Studio Image Asset Studio** kullanmanız şiddetle önerilir.

Sorularınız için Android Studio dokümantasyonuna bakabilirsiniz:
https://developer.android.com/studio/write/image-asset-studio
