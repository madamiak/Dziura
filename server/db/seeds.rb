# -*- encoding : utf-8 -*-

unit = Unit.new(:name => "ZDIUM")
unit.address = Address.create :city => "Wrocław", :street => "Jakas", :home_number => "1"
unit.save

polygon = Polygon.new(:unit => unit)

# Punkty dla Wrocławia (mniej więcej)
Point.create([
              { :polygon_id => polygon, :number => 1,
                :latitude => BigDecimal.new("51.0518656872652"),
                :longitude => BigDecimal.new("16.96879945983892") },
              { :polygon_id => polygon, :number => 2,
                :latitude => BigDecimal.new("51.04366386205539"),
                :longitude => BigDecimal.new("16.994891989135795") },
              { :polygon_id => polygon, :number => 3,
                :latitude => BigDecimal.new("51.0540238209373"),
                :longitude => BigDecimal.new("17.027164328002982") },
              { :polygon_id => polygon, :number => 4,
                :latitude => BigDecimal.new("51.05316057953732"),
                :longitude => BigDecimal.new("17.064243185424857") },
              { :polygon_id => polygon, :number => 5,
                :latitude => BigDecimal.new("51.05359220224879"),
                :longitude => BigDecimal.new("17.107501852417045") },
              { :polygon_id => polygon, :number => 6,
                :latitude => BigDecimal.new("51.07646244945305"),
                :longitude => BigDecimal.new("17.109561788940482") },
              { :polygon_id => polygon, :number => 7,
                :latitude => BigDecimal.new("51.090265430212874"),
                :longitude => BigDecimal.new("17.14458070983892") },
              { :polygon_id => polygon, :number => 8,
                :latitude => BigDecimal.new("51.11182434305764"),
                :longitude => BigDecimal.new("17.165866720581107") },
              { :polygon_id => polygon, :number => 9,
                :latitude => BigDecimal.new("51.14457463773375"),
                :longitude => BigDecimal.new("17.16380678405767") },
              { :polygon_id => polygon, :number => 10,
                :latitude => BigDecimal.new("51.167830472788246"),
                :longitude => BigDecimal.new("17.168613302612357") },
              { :polygon_id => polygon, :number => 11,
                :latitude => BigDecimal.new("51.179453994899816"),
                :longitude => BigDecimal.new("17.119174826049857") },
              { :polygon_id => polygon, :number => 12,
                :latitude => BigDecimal.new("51.17471883958771"),
                :longitude => BigDecimal.new("17.068363058471732") },
              { :polygon_id => polygon, :number => 13,
                :latitude => BigDecimal.new("51.17170530572265"),
                :longitude => BigDecimal.new("17.01549135437017") },
              { :polygon_id => polygon, :number => 14,
                :latitude => BigDecimal.new("51.16180230775276"),
                :longitude => BigDecimal.new("16.966739523315482") },
              { :polygon_id => polygon, :number => 15,
                :latitude => BigDecimal.new("51.15103576772898"),
                :longitude => BigDecimal.new("16.936527120971732") },
              { :polygon_id => polygon, :number => 16,
                :latitude => BigDecimal.new("51.127340535430086"),
                :longitude => BigDecimal.new("16.906314718627982") },
              { :polygon_id => polygon, :number => 17,
                :latitude => BigDecimal.new("51.10320198436438"),
                :longitude => BigDecimal.new("16.907688009643607") },
              { :polygon_id => polygon, :number => 18,
                :latitude => BigDecimal.new("51.08379579579124"),
                :longitude => BigDecimal.new("16.947513449096732") },
              { :polygon_id => polygon, :number => 19,
                :latitude => BigDecimal.new("51.063518414307445"),
                :longitude => BigDecimal.new("16.95781313171392") }
            ])

polygon.points << Point.all
polygon.save

User.create(:login => "test", :password => "test", :role => "admin")

Status.create [ { :name => "Nowy"},
                { :name => "W toku" },
                { :name => "Rozwiązany" },
                { :name => "Zamknięty" }]

def cat_icon(id)
  Base64.encode64(File.read('db/images/icon_category_' + id.to_s + '.jpg'))
end

Category.create [ { :id => 1, :name => "Dziury w jezdni", :icon => cat_icon(1) },
                  { :id => 2, :name => "Dziury w chodniku", :icon => cat_icon(2) },
                  { :id => 3, :name => "Uszkodzony znak drogowy", :icon => cat_icon(3) },
                  { :id => 4, :name => "Graffiti", :icon => cat_icon(4) },
                  { :id => 5, :name => "Zalegający śnieg", :icon => cat_icon(5) },
                  { :id => 6, :name => "Oblodzenie", :icon => cat_icon(6) },
                  { :id => 7, :name => "Zalana ulica", :icon => cat_icon(7) },
                  { :id => 8, :name => "Dewastacja", :icon => cat_icon(8) },
                  { :id => 9, :name => "Zaśmiecenie", :icon => cat_icon(9) },
                  { :id => 10, :name => "Zwierzęta", :icon => cat_icon(10) } ]
