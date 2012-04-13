# -*- encoding : utf-8 -*-

# Moduł zawierający wyjątki zgłaszane w aplikacji
module Exceptions

  # Brak jednostki (dodawanie nowego zgłoszenia)
  class NoUnitForPoint < StandardError
    def initialize
      super("Nie ma jednostki dla takiego punktu")
    end
  end

  # Nie podano wymaganego argumentu(ów) (dodawanie nowego zgłoszenia)
  class NilArguments < StandardError
    def initialize
      super("Nie podano wymaganego argumentu/argumentów")
    end
  end

  # Nieznana kategoria (dodawanie nowego zgłoszenia)
  class UnknownCategory < StandardError
    def initialize
      super("Nie ma takiej kategorii")
    end
  end

  # Nieprawidłowy adres e-mail (dodawanie nowego zgłoszenia)
  class IncorrectNotificarEmail < StandardError
    def initialize
      super("Niepoprawny adres e-mail")
    end
  end

  # Bład geokodowania adresu (dodawanie nowego zgłoszenia)
  class GeocodingException < StandardError
    def initialize
      super("Błąd przy ustalaniu adresu na podstawie współrzędnych")
    end
  end

  # Nieznane zgłoszenie (złączanie zgłoszeń)
  class UnknownIssue < StandardError
    def initialize
      super("Nieznane zgłoszenie")
    end
  end

end
