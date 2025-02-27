package ru.tusur.ShaurmaWebSiteProject.backend.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.flow.server.StreamResource;
import org.springframework.stereotype.Service;
import ru.tusur.ShaurmaWebSiteProject.backend.model.*;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.OrderContentRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.OrderRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ReviewRepo;
import ru.tusur.ShaurmaWebSiteProject.ui.utils.Pair;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PDFCreatorService {
    private final OrderContentRepo orderContentRepo;
    private final ReviewRepo reviewRepo;
    private final OrderRepo orderRepo;
    private final SimpleDateFormat sdf = new SimpleDateFormat();

    public PDFCreatorService(OrderContentRepo orderContentRepo, ReviewRepo reviewRepo, OrderRepo orderRepo) {
        this.orderContentRepo = orderContentRepo;

        this.reviewRepo = reviewRepo;
        this.orderRepo = orderRepo;
    }

    public StreamResource createPDFReportOrders(Date start, Date end) throws FileNotFoundException, DocumentException {
        Font font = FontFactory.getFont("src/main/resources/fonts/DejaVuSans.ttf", "cp1251", BaseFont.EMBEDDED, 10);

        // Create a temporary file
        var ref = new Object() {
            File tempFile = null;
        };
        try {
            ref.tempFile = File.createTempFile("data_report_orders", ".pdf");
            try (FileOutputStream fs = new FileOutputStream(ref.tempFile)) {
                Document document = new Document();
                PdfWriter.getInstance(document, fs);
                document.open();

                List<Order> all = orderRepo.findAllByOrderStateDateBetween(start, end);

                Paragraph paragraph = new Paragraph();
                paragraph.add(new Paragraph("Данные по заказам c " + sdf.format(start) + " по " + sdf.format(end), font));
                paragraph.add(new Paragraph("Кол-во: " + all.size(), font));
                document.add(paragraph);

                PdfPTable table = new PdfPTable(5);
                Stream.of("Товары", "Филиал", "Сумма", "Тип доставки", "Тип оплаты")
                        .forEach(columnTitle -> {
                            table.addCell(new Paragraph(columnTitle, font));
                        });

                all.stream().sorted(Comparator.comparing(Order::getId)).forEach(order -> {
                    table.addCell(new Paragraph(order
                            .getOrderContents()
                            .stream()
                            .map(orderContent ->
                                    orderContent
                                            .getProduct()
                                            .getName() + " Кол-во: " + orderContent.getNum())
                            .collect(Collectors.joining("\n")), font));
                    table.addCell(new Paragraph(order.getOrderContents().stream().map(OrderContent::getBranch).map(Branch::getAddress).collect(Collectors.joining("\n")), font));
                    table.addCell(new Paragraph(String.valueOf(order.getSum()), font));
                    table.addCell(new Paragraph(order.getDeliveryType().getString(), font));
                    table.addCell(new Paragraph(String.valueOf(order.getPayment().getPaymentType()), font));
                });
                document.add(table);
                document.close();
            }
        } catch (DocumentException | IOException e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }

        // Return the StreamResource pointing to the temporary file
        return new StreamResource("data_report_orders.pdf", () -> {
            try {
                return new FileInputStream(ref.tempFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public StreamResource createPDFReportReviews(Date start, Date end) throws FileNotFoundException, DocumentException {
        Font font = FontFactory.getFont("src/main/resources/fonts/DejaVuSans.ttf", "cp1251", BaseFont.EMBEDDED, 10);

        // Create a temporary file
        var ref = new Object() {
            File tempFile = null;
        };
        try {
            ref.tempFile = File.createTempFile("data_report_reviews", ".pdf");
            try (FileOutputStream fs = new FileOutputStream(ref.tempFile)) {
                Document document = new Document();
                PdfWriter.getInstance(document, fs);
                document.open();

                List<Review> all = reviewRepo.findAllByDateBetween(start, end);

                Paragraph paragraph = new Paragraph();
                paragraph.add(new Paragraph("Данные по отзывам c " + sdf.format(start) + " по " + sdf.format(end), font));
                paragraph.add(new Paragraph("Кол-во: " + all.size(), font));
                document.add(paragraph);

                PdfPTable table = new PdfPTable(5);
                Stream.of("Товар", "Филиал", "Оценка", "Лаков", "Дизлаков")
                        .forEach(columnTitle -> {
                            table.addCell(new Paragraph(columnTitle, font));
                        });

                all.stream().sorted(Comparator.comparing(o -> o.getProduct().getName())).forEach(review -> {
                    table.addCell(new Paragraph(review.getProduct().getName(), font));
                    table.addCell(new Paragraph(review.getBranch().getAddress(), font));
                    table.addCell(new Paragraph(String.valueOf(review.getGrade()), font));
                    table.addCell(new Paragraph(Long.toString(review.getLikes().stream().filter(likes -> likes.getLikes().equals(LikeState.LIKE)).count()), font));
                    table.addCell(new Paragraph(Long.toString(review.getLikes().stream().filter(likes -> likes.getLikes().equals(LikeState.LIKE)).count()), font));
                });
                document.add(table);
                document.close();
            }
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

        return new StreamResource("data_report_reviews.pdf", () -> {
            try {
                return new FileInputStream(ref.tempFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public StreamResource createPDFReportIncome(Date start, Date end) throws FileNotFoundException, DocumentException {
        Font font = FontFactory.getFont("src/main/resources/fonts/DejaVuSans.ttf", "cp1251", BaseFont.EMBEDDED, 10);

        // Create a temporary file
        var ref = new Object() {
            File tempFile = null;
        };
        try {
            ref.tempFile = File.createTempFile("data_report_income", ".pdf");
            try (FileOutputStream fs = new FileOutputStream(ref.tempFile)) {
                Document document = new Document();
                PdfWriter.getInstance(document, fs);
                document.open();

                List<Order> all = orderRepo.findAllByOrderStateDateBetween(start, end);

                Paragraph paragraph = new Paragraph();
                paragraph.add(new Paragraph("Данные по доходам c " + sdf.format(start) + " по " + sdf.format(end), font));
                document.add(paragraph);

                PdfPTable table = new PdfPTable(4);
                Stream.of("Категория", "Название", "Кол-во", "Сумма")
                        .forEach(columnTitle -> {
                            table.addCell(new Paragraph(columnTitle, font));
                        });

                var numFDContext = new Object() {
                    Integer numFD = 0;
                };
                var numPDContext = new Object() {
                    Integer numPD = 0;
                };
                List<Product> products = new ArrayList<>();
                List<Pair<DeliveryType, BigDecimal>> deliveries = new ArrayList<>();
                List<ProductOption> productOptions = new ArrayList<>();
                all.forEach(order -> {
                    order.getOrderContents().forEach(orderContent -> {
                        Product product1 = products.stream().filter(product2 -> product2.getName().equals(orderContent.getProduct().getName())).findFirst().orElse(null);
                        if (product1!=null){
                            product1.setPrice(product1.getPrice().add(orderContent.getProduct().getPrice().multiply(BigDecimal.valueOf(orderContent.getNum()))));
                            product1.setNum(product1.getNum() + orderContent.getNum());
                        } else {
                            orderContent.getProduct().setPrice(orderContent.getProduct().getPrice().multiply(BigDecimal.valueOf(orderContent.getNum())));
                            orderContent.getProduct().setNum(orderContent.getProduct().getNum() + orderContent.getNum());
                            products.add(orderContent.getProduct());
                        }
                    });
                        Pair<DeliveryType, BigDecimal> deliveryTypeBigDecimalPair2 = deliveries.stream().filter(deliveryTypeBigDecimalPair1 -> deliveryTypeBigDecimalPair1.getA().equals(order.getDeliveryType())).findFirst().orElse(null);
                        if (deliveryTypeBigDecimalPair2 != null) {
                            deliveries.remove(deliveryTypeBigDecimalPair2);
                            Pair<DeliveryType, BigDecimal> deliveryTypeBigDecimalPair3 = new Pair<>(deliveryTypeBigDecimalPair2.getA(), deliveryTypeBigDecimalPair2.getB().add(order.getDeliverySum()));
                            deliveries.add(deliveryTypeBigDecimalPair3);
                        } else {
                            deliveries.add(new Pair<>(order.getDeliveryType(), order.getDeliverySum()));
                        }
                        if (!Objects.equals(order.getDeliverySum(), BigDecimal.ZERO)){
                            numPDContext.numPD +=1;
                        } else {
                            numFDContext.numFD +=1;
                        }

                });

                var context = new Object() {
                    BigDecimal sum = BigDecimal.ZERO;
                };
                products.forEach(product -> {
                    table.addCell(new Paragraph("Товар", font));
                    table.addCell(new Paragraph(product.getName(), font));
                    table.addCell(new Paragraph(String.valueOf(product.getNum()), font));
                    table.addCell(new Paragraph(String.valueOf(product.getPrice()), font));
                    context.sum = context.sum.add(product.getPrice());
                        }
                );
                deliveries.forEach(deliveryTypeBigDecimalPair -> {
                    table.addCell(new Paragraph("Доставка", font));
                    if (!Objects.equals(deliveryTypeBigDecimalPair.getB(), BigDecimal.ZERO)){
                        table.addCell(new Paragraph(String.valueOf(DeliveryType.COURIER), font));
                        table.addCell(new Paragraph(String.valueOf(numPDContext.numPD), font));
                        table.addCell(new Paragraph(String.valueOf(deliveryTypeBigDecimalPair.getB()), font));
                        context.sum = context.sum.add(deliveryTypeBigDecimalPair.getB());
                    } else {
                        table.addCell(new Paragraph(String.valueOf(DeliveryType.PICK_UP), font));
                        table.addCell(new Paragraph(String.valueOf(numFDContext.numFD), font));
                        table.addCell(new Paragraph(String.valueOf(BigDecimal.ZERO), font));
                    }
                });

                table.addCell("");
                table.addCell("");
                table.addCell(new Paragraph("Всего: ", font));
                table.addCell(new Paragraph(String.valueOf(context.sum), font));

                document.add(table);
                document.close();
            }
        } catch (DocumentException | IOException e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }

        // Return the StreamResource pointing to the temporary file
        return new StreamResource("data_report_income.pdf", () -> {
            try {
                return new FileInputStream(ref.tempFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

}
