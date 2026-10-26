# Tubes PBO Ngempe 

# Komponen dan Cara pakainya

1. Button Detail
Button ini dipakai untuk menampilkan detail produk. Cara Pakai codenya

DetailButton btn1 = new DetailButton("Lihat Detail");
btn1.setPreferredSize(new Dimension(160, 45));

2. Card Product

    // Scale Image
    ImageIcon rawIcon = new ImageIcon("app\\sawit_wowo.jpg"); //Pakai relative path
    Image scaledImage = rawIcon.getImage().getScaledInstance(250, 160, Image.SCALE_SMOOTH);
    ImageIcon img = new ImageIcon(scaledImage);

    // Membuat 1 buah card
    ProductCard card = new ProductCard(img, "Original Bawang Gurih", "Rp20.000");
    card.setPreferredSize(new Dimension(310, 360));
    mainPanel.add(card);
    frame.add(mainPanel);
    frame.setVisible(true);
