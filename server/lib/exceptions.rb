# -*- encoding : utf-8 -*-
module Exceptions
  class NoUnitForPoint < StandardError
  end
  
  class NilArguments < StandardError
  end
  
  class UnknownCategory < StandardError
  end
  
  class IncorrectNotificarEmail < StandardError
  end

  class NetworkException < StandardError
  end
end
