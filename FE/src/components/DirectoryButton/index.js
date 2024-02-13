import { useNavigate } from "react-router-dom";
import { styled } from "styled-components";

const DirectoryButton = () => {
  const navigate = useNavigate();

  return (
    <ButtonFixed>
      <Button onClick={() => navigate("/directory/edit")}>+</Button>
    </ButtonFixed>
  );
};

export default DirectoryButton;

const ButtonFixed = styled.div`
  width: 100vw;
  max-width: 390px;

  position: fixed;
  bottom: 80px;

  display: flex;
  justify-content: end;
`;

const Button = styled.button`
  width: 70px;
  height: 70px;

  font-size: 2rem;
  color: #949597;

  background-color: #ffe866;

  border-radius: 50%;

  z-index: 100;

  &:hover {
    background-color: #ffd900;
  }
`;
